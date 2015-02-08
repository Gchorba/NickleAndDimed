package gk.nickles.ndimes.services;

import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;

import gk.nickles.ndimes.dataaccess.AttendeeStore;
import gk.nickles.ndimes.dataaccess.ExpenseStore;
import gk.nickles.ndimes.dataaccess.ParticipantStore;
import gk.nickles.ndimes.dataaccess.UserStore;
import gk.nickles.ndimes.model.Debt;
import gk.nickles.ndimes.model.Event;
import gk.nickles.ndimes.model.Expense;
import gk.nickles.ndimes.model.Participant;
import gk.nickles.ndimes.model.User;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

public class DebtCalculator {
    @Inject
    private ExpenseStore expenseStore;

    @Inject
    private UserStore userStore;

    @Inject
    private ParticipantStore participantStore;

    @Inject
    private AttendeeStore attendeeStore;

    @Inject
    private DebtOptimizer debtOptimizer;


    public List<Debt> calculateDebts(Event event) {
        checkNotNull(event);

        List<Debt> debts = getDebts(event);
        return debtOptimizer.optimize(debts, event.getCurrency());
    }

    private List<Debt> getDebts(Event event) {
        List<Debt> debts = new LinkedList<Debt>();
        List<Expense> expenses = expenseStore.getExpensesOfEvent(event.getId());
        for (Expense expense : expenses) {
            Participant toParticipant = participantStore.getById(expense.getPayerId());
            List<Participant> fromParticipants = attendeeStore.getAttendingParticipants(expense.getId());
            int amount = expense.getAmount() / fromParticipants.size();
            for (Participant fromParticipant : fromParticipants) {
                User fromUser = userStore.getById(fromParticipant.getUserId());
                User toUser = userStore.getById(toParticipant.getUserId());
                if (fromUser.equals(toUser)) continue;
                debts.add(new Debt(fromUser, toUser, amount, event.getCurrency()));
            }
        }

        return debts;
    }

}
