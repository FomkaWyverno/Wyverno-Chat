package ua.wyverno.twitch.api.authorization;

public class AboutAccount {

    public String displayName;
    public String profileImageURL;

    public int countFollowers;

    public AboutAccount() {

    }

    public AboutAccount(String displayName, String profileImageURL, int countFollowers) {
        this.displayName = displayName;
        this.profileImageURL = profileImageURL;
        this.countFollowers = countFollowers;
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

    public int getCountFollowers() {
        return countFollowers;
    }

    public void setCountFollowers(int countFollowers) {
        this.countFollowers = countFollowers;
    }
}
