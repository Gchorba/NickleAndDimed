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
import gk.nickles.ndimes.model.Debt;
import gk.nickles.ndimes.model.Event;
import gk.nickles.ndimes.model.Expense;
import gk.nickles.ndimes.model.ExpensePresentation;
import gk.nickles.ndimes.model.Participant;
import gk.nickles.ndimes.model.User;
import gk.nickles.splitty.R;

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class ExpenseService {

    @Inject
    private ExpenseStore expenseStore;
    @Inject
    private ParticipantStore participantStore;
    @Inject
    private UserStore userStore;
    @Inject
    private EventStore eventStore;
    @Inject
    private AttendeeStore attendeeStore;
    @Inject
    private Context context;


    public List<ExpensePresentation> getExpensePresentations(UUID eventId) {
        checkNotNull(eventId);

        Event event = eventStore.getById(eventId);
        List<Expense> expenses = expenseStore.getExpensesOfEvent(eventId);

        List<ExpensePresentation> result = new LinkedList<ExpensePresentation>();
        for (Expense expense : expenses) {
            Participant payer = participantStore.getById(expense.getPayerId());
            List<Participant> attendees = attendeeStore.getAttendingParticipants(expense.getId());

            User payingUser = userStore.getById(payer.getUserId());
            List<User> attendingUsers = new LinkedList<User>();
            for (Participant participant : attendees) {
                User user = userStore.getById(participant.getUserId());
                attendingUsers.add(user);
            }
            result.add(new ExpensePresentation(payingUser, expense, event.getCurrency(), attendingUsers));
        }

        return result;
    }

    public void createPaybackExpense(Debt debt, Event event){
        checkNotNull(debt);

        Participant payer = participantStore.getParticipant(event.getId(), debt.getFrom().getId());
        Participant attendeeParticipant = participantStore.getParticipant(event.getId(), debt.getTo().getId());
        int amount = debt.getAmount();
        String description = context.getResources().getString(R.string.paid_debt);

        Expense expense = new Expense(event.getId(), payer.getId(), description, amount, debt.getTo().getId());
        expenseStore.persist(expense);

        Attendee attendee = new Attendee(expense.getId(), attendeeParticipant.getId());
        attendeeStore.persist(attendee);
    }
}
