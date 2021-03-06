package gk.nickles.ndimes.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.inject.Inject;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import gk.nickles.ndimes.model.Event;
import gk.nickles.ndimes.model.User;
import gk.nickles.ndimes.services.ActivityStarter;
import gk.nickles.ndimes.services.EventService;
import gk.nickles.ndimes.services.LoginService;
import gk.nickles.ndimes.services.SharedPreferenceService;
import gk.nickles.ndimes.services.UserService;
import gk.nickles.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
public class Login extends RoboActivity {

    @InjectView(R.id.user_name)
    private EditText nameField;
    @InjectView(R.id.user_email)
    private EditText emailField;
    @InjectView(R.id.user_phone)
    private EditText phoneField;
    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Inject
    private ActivityStarter activityStarter;

    @Inject
    private UserService userService;

    @Inject
    private LoginService loginService;

    @Inject
    private EventService eventService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (userService.getMe() != null) {
            handleUserLoggedIn();
            return;
        }
        setContentView(R.layout.login);
        setTitle(R.string.app_name);
//        OnClickListener listnr=new OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent venmoIntent = VenmoLibrary.openVenmoPayment("2346", "NickleAndDimed", "406106603", "10", "note", "charge");
////                startActivityForResult(venmoIntent, 2346);
//              Intent myIntent = new Intent(Login.this,
//                       SampleActivity.class);
//                startActivity(myIntent);
//
//            }
//        };
//        Button btn =(Button) findViewById(R.id.btn);
//        btn.setOnClickListener(listnr);

    }


    private void handleUserLoggedIn() {
        Event event = eventService.getActiveEvent();
        if (event != null) {
            activityStarter.startEventDetails(this, event, true);
        } else {
            activityStarter.startStartEvent(this);
        }
        finish();
    }

    public void onStartClicked() {
        String userName = nameField.getText().toString();
        String userEmail = emailField.getText().toString();
        String userPhone = phoneField.getText().toString();
        if (userName == null || userName.isEmpty()) {
            nameField.setBackgroundColor(getResources().getColor(R.color.error_color));
            return;
        }

        User me = new User(userName, userEmail, userPhone);
       System.out.println(me.toString());
        loginService.login(me);

        activityStarter.startStartEvent(this);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_login_start == item.getItemId()) {
            onStartClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
