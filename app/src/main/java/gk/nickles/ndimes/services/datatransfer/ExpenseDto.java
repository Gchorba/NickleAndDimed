package gk.nickles.ndimes.services.datatransfer;

import java.util.LinkedList;
import java.util.List;

import gk.nickles.ndimes.model.Expense;

public class ExpenseDto {
    private Expense expense;
    private List<AttendeeDto> attendingParticipants = new LinkedList<AttendeeDto>();

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public List<AttendeeDto> getAttendingParticipants() {
        return attendingParticipants;
    }

    public void setAttendingParticipants(List<AttendeeDto> attendingParticipants) {
        this.attendingParticipants = attendingParticipants;
    }
}
