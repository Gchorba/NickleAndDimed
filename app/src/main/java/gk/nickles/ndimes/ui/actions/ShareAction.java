package gk.nickles.ndimes.ui.actions;

import android.content.Intent;

import com.google.inject.Inject;

import java.util.List;

import gk.nickles.ndimes.model.Debt;
import gk.nickles.ndimes.model.Event;
import gk.nickles.ndimes.services.DebtCalculator;
import gk.nickles.ndimes.services.UserService;
import gk.nickles.ndimes.ui.EventDetails;
import gk.nickles.splitty.R;

public class ShareAction implements EventDetailsAction {

    @Inject
    private DebtCalculator debtCalculator;

    @Inject
    private UserService userService;

    @Override
    public boolean execute(EventDetails activity) {

        Event event = activity.getEvent();
        if(event != null){
            String shareTemplate = activity.getString(R.string.share_template);
            Intent shareIntent = getShareIntent(event, shareTemplate);
            activity.startActivity(shareIntent);
        }
        return true;
    }

    private Intent getShareIntent(Event event, String shareTemplate) {
        List<Debt> debts = debtCalculator.calculateDebts(event);
        String text = createText(debts, shareTemplate);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");

        return intent;
    }

    private String createText(List<Debt> debts, String shareTemplate) {
        StringBuilder debtsText = new StringBuilder();
        boolean first = true;
        for (Debt debt : debts) {
            if (!first) {
                debtsText.append("\n\r");
            }
            debtsText.append(debt.toString());

            first = false;
        }
        String username = userService.getMe().getName();
        return String.format(shareTemplate, debtsText.toString(), username);
    }
}
