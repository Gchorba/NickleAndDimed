package gk.nickles.ndimes.services;

import android.content.Context;

import com.google.inject.Inject;

import java.util.List;

import gk.nickles.ndimes.dataaccess.AttendeeStore;
import gk.nickles.ndimes.dataaccess.EventStore;
import gk.nickles.ndimes.dataaccess.ExpenseStore;
import gk.nickles.ndimes.dataaccess.ParticipantStore;
import gk.nickles.ndimes.dataaccess.UserStore;
import gk.nickles.ndimes.model.Attendee;
import gk.nickles.ndimes.model.Expense;
import gk.nickles.ndimes.model.Participant;
import roboguice.util.RoboAsyncTask;

public class MigrationService {

    @Inject
    private SharedPreferenceService sharedPreferenceService;
    @Inject
    private ParticipantStore participantStore;
    @Inject
    private ExpenseStore expenseStore;
    @Inject
    private AttendeeStore attendeeStore;
    @Inject
    private UserStore userStore;
    @Inject
    private EventStore eventStore;

    public void migrateToVersion300() {
        addPayerAsAttendeeForAllExpenses();
    }

    private void addPayerAsAttendeeForAllExpenses() {
        List<Expense> expenses = expenseStore.getAll();
        for (Expense expense : expenses) {
            Participant payerParticipant = participantStore.getById(expense.getPayerId());

            if (hasAttendeeForPayer(expense, payerParticipant)) continue;

            Attendee attendee = new Attendee(expense.getId(), payerParticipant.getId());
            attendeeStore.persist(attendee);
        }
    }

    private boolean hasAttendeeForPayer(Expense expense, Participant participant) {
        List<Participant> participants = attendeeStore.getAttendingParticipants(expense.getId());
        return participants.contains(participant);
    }

    public void migrateToVersion(final int versionCode, Context context) {
        (new RoboAsyncTask<Void>(context) {
            @Override
            public Void call() throws Exception {
                int oldVersionCode = getOldVersionCode();

                if (oldVersionCode < 300 && versionCode >= 300) {
                    migrateToVersion300();
                }

                sharedPreferenceService.storeCurrentVersionCode(versionCode);
                return null;
            }

            private int getOldVersionCode() {
                Integer oldVersionCode = sharedPreferenceService.getCurrentVersionCode();

                // Version Code was introduced in version 300.
                // If no version code exists assume it was last version before 300
                if (oldVersionCode == null) oldVersionCode = 210;

                return oldVersionCode;
            }
        }).execute();
    }
}