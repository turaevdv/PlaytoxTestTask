package ru.turaev;

import lombok.extern.slf4j.Slf4j;
import ru.turaev.entity.Account;
import ru.turaev.exception.NotEnoughMoneyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Main {
    private static final int numberOfAccounts = 4;
    private static final int numberOfThreads = 2;
    private static final int numberOfRepetitions = 30;

    public static void main(String[] args) {
        log.info("Application start");
        List<Account> accounts = new ArrayList<>();

        for (int i = 0; i < numberOfAccounts; i++) {
            accounts.add(new Account());
        }

        log.info("The total amount of money = {}", 10_000 * accounts.size());

        AtomicInteger atomicInteger = new AtomicInteger();

        Runnable task = () -> {
            Random random = new Random();
            while (atomicInteger.get() < numberOfRepetitions) {
                atomicInteger.incrementAndGet();
                int i = random.nextInt(accounts.size());
                int j;
                do {
                    j = random.nextInt(accounts.size());
                } while (i == j);

                Account accountFrom = accounts.get(i);
                Account accountTo = accounts.get(j);
                try {
                    Thread.sleep(random.nextInt(1000) + 1000);
                    accountFrom.transferTo(accountTo, random.nextInt(10_000));
                } catch (NotEnoughMoneyException | UnsupportedOperationException | InterruptedException e) {
                    atomicInteger.decrementAndGet();
                }
            }
            log.info("The thread has finished executing");
        };

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            Thread thread = new Thread(task);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                log.error("Interrupted exception in thread {} ", thread.getName());
            }
        }

        int sum = 0;
        for (Account account : accounts) {
            log.info("Account with id = {} has money = {}", account.getId(), account.getMoney());
            sum += account.getMoney();
        }

        log.info("The total amount of money = {}", sum);
        log.info("Application finish\n");
    }
}
