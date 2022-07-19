import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BankTransferTest extends TestCase {
    private Bank bank;
    private List<Thread> threads;
    private final Random random = new Random();

    @Override
    protected void setUp() {
        bank = new Bank();
        threads = new ArrayList<>();

        for (int i = 1_000_001; i < 1_001_001; i++) {
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
    }

    public void testBannedAccounts() {
        Account account1 = bank.getAccount("1000001");
        Account account2 = bank.getAccount("1000002");
        account1.setBanned(true);
        account2.setBanned(true);
        long expected = account1.getMoney();
        bank.transfer("1000001", "1000002", 100000);
        long actual = account1.getMoney();
        bank.clearBans();
        assertEquals(expected, actual);
    }

    public void testIsFraudCheck() throws InterruptedException {
        Account account1 = bank.getAccount("1000001");
        while (!account1.isBanned()) {
            account1.setMoney(1_000_000);
            new Thread(() ->
                    bank.transfer("1000001", "1000002", 100_000)
            ).start();

            new Thread(() -> {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bank.transfer("1000001", "1000002", 1000);
            }).start();
            Thread.sleep(1500);
        }
        Thread.sleep(1000);
        long actual = bank.getBalance("1000001");
        long expected = 900_000;
        bank.clearBans();
        assertEquals(expected, actual);
    }

    public void testDeadLock() throws InterruptedException {
       Thread thread1 = new Thread(() -> {
            for (int i = 1; i < 10001; i++) {
                bank.transfer("1000001", "1000002", 10);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 1; i < 10001; i++) {
                bank.transfer("1000002", "1000001", 1);
            }
        });

        thread1.start();
        thread2.start();
        thread1.join(3000);
        thread2.join(3000);
        long actual = bank.getBalance("1000001");
        long expected = 910000;
        assertEquals(expected, actual);
    }

    public void testHighLoaded() {
        long expected = bank.getSumAllAccounts();
        threads.forEach(thread -> thread.start());
        threads.forEach(thread -> {
            try {
                thread.join(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long actual = bank.getSumAllAccounts();
        bank.clearBans();
        assertEquals(expected, actual);

    }


}

