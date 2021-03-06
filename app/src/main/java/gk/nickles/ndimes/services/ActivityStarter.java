package gk.nickles.ndimes.services;

import android.content.Context;
import android.content.Intent;

import com.google.inject.Singleton;

import gk.nickles.ndimes.model.Event;
import gk.nickles.ndimes.model.Expense;
import gk.nickles.ndimes.ui.AddEvent;
import gk.nickles.ndimes.ui.AddParticipants;
import gk.nickles.ndimes.ui.AddExpense;
import gk.nickles.ndimes.ui.BeamEvent;
import gk.nickles.ndimes.ui.BillSplitterSettings;
import gk.nickles.ndimes.ui.EventDetails;
import gk.nickles.ndimes.ui.Login;
import gk.nickles.ndimes.ui.StartEvent;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static gk.nickles.ndimes.ui.AddParticipants.EVENT_ID;
import static gk.nickles.ndimes.ui.AddExpense.ARGUMENT_EXPENSE_ID;
import static gk.nickles.ndimes.ui.EventDetails.ARGUMENT_EVENT_ID;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class ActivityStarter {

    public void startLogin(Context context) {
        checkNotNull(context);

        Intent intent = new Intent(context, Login.class);
        context.startActivity(intent);
    }

    public void startAddEvent(Context context) {
        checkNotNull(context);

        Intent intent = new Intent(context, AddEvent.class);
        context.startActivity(intent);
    }

    public void startEditEvent(Context context, Event event) {
        checkNotNull(context);
        checkNotNull(event);

        Intent intent = new Intent(context, AddEvent.class);
        intent.putExtra(ARGUMENT_EVENT_ID, event.getId());
        context.startActivity(intent);
    }

    public void startEventDetails(Context context, Event event, boolean clearBackStack) {
        checkNotNull(context);
        checkNotNull(event);

        Intent intent = new Intent(context, EventDetails.class);
        intent.putExtra(ARGUMENT_EVENT_ID, event.getId());
        if(clearBackStack){
            intent.setFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public void startAddExpense(Context context, Event event) {
        checkNotNull(context);
        checkNotNull(event);

        Intent intent = new Intent(context, AddExpense.class);
        intent.putExtra(ARGUMENT_EVENT_ID, event.getId());
        context.startActivity(intent);
    }

    public void startEditExpense(Context context, Expense expense) {
        checkNotNull(context);
        checkNotNull(expense);

        Intent intent = new Intent(context, AddExpense.class);
        intent.putExtra(ARGUMENT_EXPENSE_ID, expense.getId());
        context.startActivity(intent);
    }

    public void startStartEvent(Context context) {
        checkNotNull(context);

        Intent intent = new Intent(context, StartEvent.class);
        context.startActivity(intent);
    }

    public void startAddParticipants(Context context, Event event) {
        checkNotNull(context);
        checkNotNull(event);

        Intent intent = new Intent(context, AddParticipants.class);
        intent.putExtra(EVENT_ID, event.getId());
        context.startActivity(intent);
    }

    public void startSettings(Context context){
        checkNotNull(context);

        Intent intent = new Intent(context, BillSplitterSettings.class);
        context.startActivity(intent);
    }

    public void startBeamEvent(Context context, Event event) {
        checkNotNull(context);
        checkNotNull(event);

        Intent intent = new Intent(context, BeamEvent.class);
        intent.putExtra(BeamEvent.ARGUMENT_EVENT_ID, event.getId());
        context.startActivity(intent);
    }
}
