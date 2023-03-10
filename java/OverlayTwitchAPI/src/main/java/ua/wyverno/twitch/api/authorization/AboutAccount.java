package ua.wyverno.twitch.api.authorization;

public class AboutAccount {

    public String displayName;
    public String profileImageURL;

    public AboutAccount() {

    }

    public AboutAccount(String displayName, String profileImageURL) {
        this.displayName = displayName;
        this.profileImageURL = profileImageURL;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }
}
