package gk.nickles.ndimes.services;

import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

import gk.nickles.ndimes.dataaccess.TagStore;
import gk.nickles.ndimes.dataaccess.UserStore;
import gk.nickles.ndimes.model.Tag;
import gk.nickles.ndimes.model.User;
import gk.nickles.splitty.R;

import static java.util.Arrays.asList;

@Singleton
public class LoginService {

    @Inject
    private UserStore userStore;

    @Inject
    private SharedPreferenceService sharedPreferenceService;

    @Inject
    private TagStore tagStore;

   @Inject
   private Context context;

    public void login(User me){
        if(me.isNew()){
            userStore.persist(me);
        } else {
            // This is used if an existing user object is used
            // as part of the beam / synchronize process
            userStore.createExistingModel(me);
        }
        sharedPreferenceService.storeUserId(me.getId());
        createStandardTags();
    }

    private void createStandardTags() {
        List<Integer> tags = asList(
                R.string.tag_food,
                R.string.tag_drinks,
                R.string.tag_shopping,
                R.string.tag_party,
                R.string.tag_hotel,
                R.string.tag_flight,
                R.string.tag_museum);

        for (int tag : tags) {
            String name = context.getString(tag);
            if (tagStore.getTagWithName(name) == null) {
                tagStore.persist(new Tag(name));
            }
        }
    }

}
