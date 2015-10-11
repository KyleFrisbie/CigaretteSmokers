import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Created by Kyle L Frisbie on 10/9/2015.
 */
public class CigaretteSmokers {

    // create smokers with unique ingredients
    Smoker paperSmoker = new Smoker();
    Smoker tobaccoSmoker = new Smoker();
    Smoker matchSmoker = new Smoker();

    // create agent to place ingredients
    Agent agent = new Agent();

    // create mutex
    static Semaphore mutex = new Semaphore(1);

    // ingredient combinations placed by agent
    static Semaphore paperAndTobacco = new Semaphore(0);
    static Semaphore paperAndMatches = new Semaphore(0);
    static Semaphore tobaccoAndMatches = new Semaphore(0);

    private class Agent {
        boolean readyForNextItem = true;

        Random random = new Random();

        public void placeItems() throws InterruptedException {

            // agent is placing items so not ready to place more
            readyForNextItem = false;

            // randomly generate items to place
            int randomIngredients = random.nextInt(3);
            switch (randomIngredients) {
                case 0:
                    paperAndTobacco.release();
                    break;
                case 1:
                    paperAndMatches.release();
                    break;
                case 2:
                    tobaccoAndMatches.release();
                    break;
            }
        }

        // receive signal from current smoker that they have finished smoking
        public void signalCompletion() {
            readyForNextItem = true;
        }

        public boolean getReadyForNextItem() {
            return readyForNextItem;
        }
    }

    private class Smoker {

        // lock mutex and acquire ingredients
        public void getIngredients(Semaphore ingredient) throws InterruptedException {
            mutex.acquire();
            ingredient.acquire();
        }

        // smoke that cigarette
        public void smoke(String name) {
            System.out.println(name + " is smoking their cigarette.");
        }

        // let agent know smoking is complete and release mutex
        public void signalCompletion() {
            agent.signalCompletion();
            mutex.release();
        }
    }

    public void smokeAway() throws InterruptedException {

        while (true) {

            // if the mutex is available and the agent is ready to place an item
            if (mutex.availablePermits() > 0 && agent.getReadyForNextItem()) {
                agent.placeItems();

                if (paperAndTobacco.availablePermits() > 0 ) {
                    matchSmoker.getIngredients(paperAndTobacco);
                    matchSmoker.smoke("Smoker with matches");
                    matchSmoker.signalCompletion();
                } else if (paperAndMatches.availablePermits() > 0 ) {
                    tobaccoSmoker.getIngredients(paperAndMatches);
                    tobaccoSmoker.smoke("Smoker with tobacco");
                    tobaccoSmoker.signalCompletion();
                } else {
                    paperSmoker.getIngredients(tobaccoAndMatches);
                    paperSmoker.smoke("Smoker with paper");
                    paperSmoker.signalCompletion();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CigaretteSmokers cs = new CigaretteSmokers();

        cs.smokeAway();
    }
}