package fight.ia;

import fight.Fight;
import fight.Fighter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Locos on 18/09/2015.
 */
public abstract class AbstractIA implements IA {

    private final ScheduledExecutorService executor;

    protected Fight fight;
    protected Fighter fighter;
    protected boolean stop;
    protected byte count;

    public AbstractIA(Fight fight, Fighter fighter, byte count, String IA) {
        this.fight = fight;
        this.fighter = fighter;
        this.count = count;
        this.executor = Executors.newSingleThreadScheduledExecutor( r -> {
            Thread thread = new Thread(r);
            //thread.setDaemon(true);
            thread.setName(IA);
            return thread;
        });
    }

    public Fight getFight() {
        return fight;
    }

    public Fighter getFighter() {
        return fighter;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public void endTurn() {
        if (this.stop && !this.fighter.isDead()) {
            if (this.fighter.haveInvocation()) {
                this.addNext(() -> {
                    this.fight.endTurn(false, this.fighter);
                    this.executor.shutdownNow();
                }, 100);
            } else {
                this.fight.endTurn(false, this.fighter);
                this.executor.shutdownNow();
            }
        } else {
            if(this.fighter.isDead())
                this.executor.shutdownNow();

            if(!this.fight.isFinish())
                this.addNext(this::endTurn, 50);
            else
                this.executor.shutdownNow();
        }
    }

    protected void decrementCount() {
        this.count--;
        this.apply();
    }

    protected void nondecrementCount() {
        this.apply();
    }

    private static final double SPEED_FACTOR = 2.5; // >1 = tours de monstres plus rapides
    private static final int MIN_DELAY_MS = 150;     // ancien floor 500, réduit proportionnellement à SPEED_FACTOR

    public void addNext(Runnable runnable, Integer time) {
        /*while(this.fight.isCurAction() || this.fight.isTraped())
            try {
                time -= 20;
                Thread.sleep(20);
            } catch (InterruptedException e) {}*/
        int scaled = (int) Math.round(time / SPEED_FACTOR);
        executor.schedule(runnable,scaled < MIN_DELAY_MS ? MIN_DELAY_MS : scaled,TimeUnit.MILLISECONDS);
    }
}
