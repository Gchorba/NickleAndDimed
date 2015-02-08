package gk.nickles.ndimes.services;

import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.LinkedList;
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
import gk.nickles.ndimes.services.datatransfer.EventDto;
import gk.nickles.ndimes.services.datatransfer.EventDtoBuilder;
import gk.nickles.ndimes.services.datatransfer.ExpenseDto;
import gk.nickles.ndimes.services.datatransfer.ParticipantDto;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static java.lang.System.currentTimeMillis;
import static roboguice.RoboGuice.getInjector;

@Singleton
public class ExportService {

    @Inject
    private EventStore eventStore;

    @Inject
    private ParticipantStore participantStore;

    @Inject
    private AttendeeStore attendeeStore;

    @Inject
    private ExpenseStore expenseStore;

    @Inject
    private UserService userService;

    @Inject
    private UserStore userStore;

    @Inject
    private Context context;

    public EventDto exportEvent(UUID eventId) {
        checkNotNull(eventId);

        EventDtoBuilder builder = getInjector(context).getInstance(EventDtoBuilder.class);

        Event event = eventStore.getById(eventId);
        builder.withEvent(event);

        builder.withParticipants(getParticipantDtos(eventId));

        builder.withExpenses(getExpenseDtos(eventId));

        return builder.build();
    }

    private List<ExpenseDto> getExpenseDtos(UUID eventId) {
        List<ExpenseDto> expenseDtos = new LinkedList<ExpenseDto>();
        List<Expense> expenses = expenseStore.getExpensesOfEvent(eventId);
        for(Expense expense : expenses){
            ExpenseDto expenseDto = new ExpenseDto();
            expenseDto.setExpense(expense);
            expenseDto.setAttendingParticipants(getAttendeeDtos(expense.getId()));
            expenseDtos.add(expenseDto);
        }

        return expenseDtos;
    }

    private List<AttendeeDto> getAttendeeDtos(UUID expenseId){
        List<AttendeeDto> attendingParticipants = new LinkedList<AttendeeDto>();
        List<Attendee> attendees = attendeeStore.getAttendees(expenseId);
        for (Attendee attendee : attendees) {
            AttendeeDto attendeeDto = new AttendeeDto();
            attendeeDto.setAttendeeId(attendee.getId());
            attendeeDto.setParticipantId(attendee.getParticipant());
            attendingParticipants.add(attendeeDto);
        }

        return attendingParticipants;
    }

    private List<ParticipantDto> getParticipantDtos(UUID eventId) {

        List<ParticipantDto> participantDtos = new LinkedList<ParticipantDto>();
        User me = userService.getMe();
        List<Participant> participants = participantStore.getParticipants(eventId);

        for (Participant participant : participants) {
            ParticipantDto participantDto = new ParticipantDto();
            participantDto.setParticipantId(participant.getId());

            User user = userStore.getById(participant.getUserId());
            participantDto.setUser(user);

            participantDto.setConfirmed(participant.isConfirmed());
            long lastUpdated = user.equals(me) ?
                    currentTimeMillis() : participant.getLastUpdated();
            participantDto.setLastUpdated(lastUpdated);

            participantDtos.add(participantDto);
        }

        return participantDtos;
    }
}
