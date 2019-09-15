package fr.customentity.investment.schedulers;

import fr.customentity.investment.Investment;

import java.util.TimerTask;

/**
 * Created by CustomEntity on 27/04/2019 for [SpigotMc] Investment.
 */
public class DailyTask extends TimerTask{

    @Override
    public void run() {
        Investment.getInstance().getDatabaseSQL().resetTimeStayedToday();
    }
}
