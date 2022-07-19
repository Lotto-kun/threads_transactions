import java.util.*;

public class Bank {

    private Map<String, Account> accounts = new Hashtable<>();
    private final Random random = new Random();
    private static final long SUSPICIOUS_AMOUNT = 50000;

    public synchronized boolean isFraud(String fromAccountNum, String toAccountNum, long amount)
            throws InterruptedException {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    public void transfer(String fromAccountNum, String toAccountNum, long amount) {
        Account fromAccount = getAccount(fromAccountNum);
        Account toAccount = getAccount(toAccountNum);
        synchronized (fromAccountNum.compareTo(toAccountNum) > 0 ? fromAccount : toAccount) {
            synchronized (fromAccountNum.compareTo(toAccountNum) > 0 ? toAccount : fromAccount) {
                if (fromAccount.isBanned() || toAccount.isBanned()) {
                    return;
                }
                if (fromAccount.getMoney() - amount < 0) {
                    return;
                }

                fromAccount.setMoney(fromAccount.getMoney() - amount);
                toAccount.setMoney(toAccount.getMoney() + amount);

                if (amount >= SUSPICIOUS_AMOUNT) {
                    try {
                        if (isFraud(fromAccountNum, toAccountNum, amount)) {
                            fromAccount.setBanned(true);
                            toAccount.setBanned(true);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }

    public long getBalance(String accountNum) {
        return getAccount(accountNum).getMoney();
    }

    public long getSumAllAccounts() {
        long sum = 0;
        for (String key : accounts.keySet()) {
            sum += accounts.get(key).getMoney();
        }
        return sum;
    }

    public Account getAccount(String accountNum) {
        return accounts.get(accountNum);

    }

    public void addAccount(Account account) {
        if (!accounts.containsKey(account.getAccNumber())) {
            accounts.put(account.getAccNumber(), account);
        }
    }

    public void clearBans() {
        for (String key : accounts.keySet()) {
            accounts.get(key).setBanned(false);
        }
    }
}
