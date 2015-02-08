package gk.nickles.ndimes.services;

import com.google.inject.Inject;

import java.util.List;
import java.util.UUID;

import gk.nickles.ndimes.dataaccess.AttendeeStore;
import gk.nickles.ndimes.dataaccess.EventStore;
import gk.nickles.ndimes.dataaccess.ExpenseStore;
import gk.nickles.ndimes.dataaccess.ParticipantStore;
import gk.nickles.ndimes.dataaccess.UserStore;
import gk.nickles.ndimes.model.Attendee;
import gk.nickles.ndimes.model.Event;
import gk.nickles.ndimes.model.Expense;
import gk.nickles.ndimes.model.Participant;
import gk.nickles.ndimes.model.User;
import gk.nickles.ndimes.services.datatransfer.AttendeeDto;
import gk.nickles.ndimes.services.datatransfer.EventDtoOperator;
import gk.nickles.ndimes.services.datatransfer.ExpenseDto;
import gk.nickles.ndimes.services.datatransfer.ParticipantDto;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class ImportService {

    @Inject
    private UserService userService;
    @Inject
    private EventStore eventStore;
    @Inject
    private ExpenseStore expenseStore;
    @Inject
    private AttendeeStore attendeeStore;
    @Inject
    private ParticipantStore participantStore;
    @Inject
    private UserStore userStore;

    public void importEvent(EventDtoOperator eventDto) {
        checkNotNull(eventDto);

        User me = userService.getMe();
        Event event = eventDto.getEvent();

        createEventIfNotExists(event);

        if (!event.getOwnerId().equals(me.getId())) {
            eventStore.persist(event);
        }

        importParticipants(eventDto);

        for (ParticipantDto participantDto : eventDto.getParticipants()) {
            Participant participant = participantStore.getById(participantDto.getParticipantId());
            checkNotNull(participant, "Participants must have been imported.");

            // Never touch my own expenses
            if (participant.getUserId().equals(me.getId())) continue;

            if (participant.getLastUpdated() < participantDto.getLastUpdated()) {
                removeExpensesOfOwner(eventDto.getEvent(), participant.getUserId());
                importExpensesOfOwner(eventDto, participant.getUserId());
                participant.setLastUpdated(participantDto.getLastUpdated());
                participantStore.persist(participant);
            }
        }
    }

    private void removeExpensesOfOwner(Event event, UUID userId) {
        if (userId == null) return;

        List<Expense> expenses = expenseStore.getExpensesOfEvent(event.getId(), userId);
        for (Expense expense : expenses) {
            attendeeStore.removeAll(expense.getId());
        }

        expenseStore.removeAll(event.getId(), userId);
    }

    private void importExpensesOfOwner(EventDtoOperator eventDto, UUID ownerUserId) {
        for (ExpenseDto expenseDto : eventDto.getExpensesOfOwner(ownerUserId)) {
            Expense expense = expenseDto.getExpense();
            expenseStore.createExistingModel(expense);

            for (AttendeeDto attendeeDto : expenseDto.getAttendingParticipants()) {
                Attendee attendee = new Attendee(attendeeDto.getAttendeeId(), expense.getId(), attendeeDto.getParticipantId());
                attendeeStore.createExistingModel(attendee);
            }
        }
    }

    private User importParticipant(ParticipantDto participantDto, Event event) {
        Participant participant = participantStore.getById(participantDto.getParticipantId());

        if (participant == null) {
            return createParticipant(participantDto, event);
        } else {
            return updateParticipant(participant, participantDto);
        }
    }

    private User updateParticipant(Participant participant, ParticipantDto participantDto) {

        User newUser = participantDto.getUser();

        User oldUser = userStore.getById(participant.getUserId());
        if (!oldUser.equals(newUser)) {
            removeIfPossible(oldUser);
        }

        createUserIfNotExists(newUser);

        participant.setConfirmed(participantDto.isConfirmed());
        participant.setUserId(participantDto.getUser().getId());
        participantStore.persist(participant);

        return newUser;
    }

    private User createParticipant(ParticipantDto dto, Event event) {
        User newUser = dto.getUser();
        createUserIfNotExists(newUser);

        Participant newParticipant = new Participant(dto.getParticipantId(), dto.getUser().getId(), event.getId(), dto.isConfirmed(), 0);
        participantStore.createExistingModel(newParticipant);

        return newUser;
    }

    private void createUserIfNotExists(User user) {
        User existingUser = userStore.getById(user.getId());
        if (existingUser == null) {
            String username = userService.findBestFreeNameForUser(user);
            user.setName(username);
            userStore.createExistingModel(user);
        }
    }

    private void removeIfPossible(User user) {
        if (!participatesInMultipleEvents(user)) {
            userStore.removeById(user.getId());
        }
    }

    private boolean participatesInMultipleEvents(User user) {
        return participantStore.getParticipantsForUsers(user.getId()).size() > 1;
    }

    private void createEventIfNotExists(Event event) {
        Event existingEvent = eventStore.getById(event.getId());
        if (existingEvent == null) {
            eventStore.createExistingModel(event);
        }
    }

    private void importParticipants(EventDtoOperator eventDto) {
        for (ParticipantDto participantDto : eventDto.getParticipants()) {
            importParticipant(participantDto, eventDto.getEvent());
        }
    }
}
