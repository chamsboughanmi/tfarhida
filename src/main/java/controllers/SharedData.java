package controllers;

public class SharedData {
    private static int userId;

    private static int claimId;

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int user) {
        SharedData.userId = user;
    }

    public static int getClaimId() {
        return claimId;
    }

    public static void setClaimId(int claim) {
        SharedData.claimId = claim;
    }
}
