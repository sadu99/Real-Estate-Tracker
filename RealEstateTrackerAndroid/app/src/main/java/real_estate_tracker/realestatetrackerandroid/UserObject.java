package real_estate_tracker.realestatetrackerandroid;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Sanhar on 2017-04-01.
 */

public class UserObject {
    private String mUserName,mUserEmail,mUserID;
    private FirebaseUser mUser;

    UserObject(){
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public String getUserID(){
        if (mUserID == null){
            mUserID = mUser.getUid();
        }
        return mUserID;
    }

    public String getUserName(){
        if (mUserName == null){
            mUserName = mUser.getDisplayName();
        }
        return mUserName;
    }

    public String getUserEmail(){
        if (mUserEmail == null){
            mUserEmail = mUser.getEmail();
        }
        return mUserEmail;
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

    public String getFacebookProfilePicture(){
       return "https://graph.facebook.com/" + getUserID() + "/picture?type=large";
    }
}
