package fr.customentity.investment.data.investments;


import java.util.Set;

public class Investment {

    private Investment.Type type;
    private String name;
    private double amountToInvest;
    private double reward;
    private int timeToStay;
    private Set<String> commandsToExecute;

    public Investment(Type type, String name, double amountToInvest, double reward, int timeToStay, Set<String> commandsToExecute) {
        this.type = type;
        this.name = name;
        this.amountToInvest = amountToInvest;
        this.reward = reward;
        this.timeToStay = timeToStay;
        this.commandsToExecute = commandsToExecute;
    }

    public int getTimeToStay() {
        return timeToStay;
    }

    public double getAmountToInvest() {
        return amountToInvest;
    }

    public double getReward() {
        return reward;
    }

    public Set<String> getCommandsToExecute() {
        return commandsToExecute;
    }

    public String getName() {
        return name;
    }

    public void setAmountToInvest(long amountToInvest) {
        this.amountToInvest = amountToInvest;
    }

    public void setCommandsToExecute(Set<String> commandsToExecute) {
        this.commandsToExecute = commandsToExecute;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReward(long reward) {
        this.reward = reward;
    }

    public void setTimeToStay(int timeToStay) {
        this.timeToStay = timeToStay;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    enum Type {
        EXP,
        MONEY
    }
}
