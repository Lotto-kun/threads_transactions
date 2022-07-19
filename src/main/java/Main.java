import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        List<Thread> threads = new ArrayList<>();
        final Random random = new Random();

        for (long i = 1_000_001; i < 1_001_001; i++) {
            bank.addAccount(new Account(String.valueOf(i), 1000000L));
        }
        for (int i = 0; i < 10; i++) {
            threads.add(new Thread(()-> {
                for (int j = 0; j < 5000; j++) {
                    String account1 = String.valueOf(random.nextInt(1_000_001, 1_001_000));
                    String account2 = String.valueOf(random.nextInt(1_000_001, 1_001_000));
                    long amount = random.nextInt(1, 52630);
                    bank.transfer(account1, account2, amount);
                }
            }));
        }
        long expected = bank.getSumAllAccounts();
        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long actual = bank.getSumAllAccounts();
        bank.clearBans();
        System.out.println(expected + "  " + actual);









    }
}
