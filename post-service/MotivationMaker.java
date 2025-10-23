import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * MotivationMaker
 * - Console + simple Swing popups
 * - Create avoidance-based plans, save "penalty contract", schedule if-then reminders,
 *   start 2-minute visualization with breathing prompts, copy accountability messages to clipboard.
 *
 * Java 8+
 *
 * Notes:
 * - This program cannot actually transfer money or send SMS/emails by itself.
 * - It writes a penalty log and copies texts to clipboard so you can paste them into messages.
 */
public class MotivationMaker {
    static Scanner sc = new Scanner(System.in);
    static DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
    static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    static Plan currentPlan = null;

    public static void main(String[] args) {
        System.out.println("=== MotivationMaker (avoidance -> activation) ===");
        while (true) {
            showMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": createPlan(); break;
                case "2": viewPlan(); break;
                case "3": generateMessages(); break;
                case "4": savePenaltyContract(); break;
                case "5": scheduleIfThenReminder(); break;
                case "6": startTwoMinuteVisualization(); break;
                case "7": copyAccountabilityToClipboard(); break;
                case "0":
                    System.out.println("Exit. Good luck taking action!");
                    scheduler.shutdownNow();
                    System.exit(0);
                default:
                    System.out.println("Please choose a menu number.");
            }
        }
    }

    static void showMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Create / edit plan (Worst outcome, penalty, if-then...)");
        System.out.println("2. View current plan");
        System.out.println("3. Generate messages / if-then / penalty text (copy-paste)");
        System.out.println("4. Save 'penalty contract' to file");
        System.out.println("5. Schedule If-Then reminder (popup at specified time)");
        System.out.println("6. Start 2-minute visualization + breathing (timer)");
        System.out.println("7. Copy accountability message to clipboard");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
    }

    static void createPlan() {
        Plan p = new Plan();
        System.out.println("\n--- Create Plan ---");
        System.out.print("Short scenario description (e.g. 'Interview / Gym / Go out'): ");
        p.scenario = sc.nextLine().trim();
        System.out.print("Worst realistic outcome (one sentence): ");
        p.worstOutcome = sc.nextLine().trim();
        System.out.print("Monetary cost (VND or 0): ");
        p.costMoney = readDouble();
        System.out.print("Time cost (weeks, or 0): ");
        p.costWeeks = readDouble();
        System.out.print("Psychological impact (short): ");
        p.psychImpact = sc.nextLine().trim();
        System.out.print("Penalty you commit (e.g. transfer 200k / no Netflix 1 week): ");
        p.penalty = sc.nextLine().trim();
        System.out.print("If-Then time (HH:mm) to trigger reminder, or leave blank: ");
        String t = sc.nextLine().trim();
        if (!t.isEmpty()) {
            try {
                p.ifThenTime = LocalTime.parse(t, timeFmt);
                System.out.print("If-Then action (e.g. 'PUT ON CLOTHES & LEAVE within 5 minutes'): ");
                p.ifThenAction = sc.nextLine().trim();
            } catch (Exception e) {
                System.out.println("Cannot parse time. Skipping If-Then.");
            }
        }
        System.out.print("Accountability person (name/phone/email): ");
        p.accountability = sc.nextLine().trim();
        System.out.print("Small reward after completion (e.g. coffee): ");
        p.reward = sc.nextLine().trim();

        currentPlan = p;
        System.out.println("Plan saved temporarily in the program.");
    }

    static double readDouble() {
        while (true) {
            String s = sc.nextLine().trim();
            try {
                return Double.parseDouble(s);
            } catch (Exception e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }

    static void viewPlan() {
        if (currentPlan == null) {
            System.out.println("No plan yet. Choose 1 to create one.");
            return;
        }
        System.out.println("\n--- Current plan ---");
        System.out.println(currentPlan);
    }

    static void generateMessages() {
        if (currentPlan == null) {
            System.out.println("No plan yet. Create one first (menu 1).");
            return;
        }
        System.out.println("\n--- Generated content ---");
        System.out.println("1) Worst outcome (entered):\n" + currentPlan.worstOutcome + "\n");
        System.out.println("2) Cost summary:");
        System.out.printf("   - Money: %,.0f VND\n", currentPlan.costMoney);
        System.out.printf("   - Time: %.1f weeks\n", currentPlan.costWeeks);
        System.out.println("   - Psychological: " + currentPlan.psychImpact + "\n");
        System.out.println("3) Penalty (self-imposed):\n   " + currentPlan.penalty + "\n");
        System.out.println("4) If-Then rule:");
        if (currentPlan.ifThenTime != null) {
            System.out.println("   If " + currentPlan.ifThenTime.format(timeFmt) + " then " + currentPlan.ifThenAction);
        } else System.out.println("   No If-Then set.");
        System.out.println("\n5) Accountability message (copy-paste):");
        System.out.println("   " + currentPlan.makeAccountabilityMessage());
        System.out.println("\n6) Penalty contract template (copy-paste):");
        System.out.println("   " + currentPlan.makePenaltyContractText());
    }

    static void savePenaltyContract() {
        if (currentPlan == null) {
            System.out.println("No plan yet. Create one first (menu 1).");
            return;
        }
        System.out.print("Enter filename to save (e.g. penalty_contract.txt): ");
        String fname = sc.nextLine().trim();
        if (fname.isEmpty()) fname = "penalty_contract.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fname, false))) {
            bw.write(currentPlan.makePenaltyContractText());
            bw.newLine();
            System.out.println("Saved penalty contract -> " + fname);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    static void scheduleIfThenReminder() {
        if (currentPlan == null || currentPlan.ifThenTime == null) {
            System.out.println("No If-Then set. Create plan (menu 1) and enter HH:mm.");
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = LocalDateTime.of(LocalDate.now(), currentPlan.ifThenTime);
        if (next.isBefore(now) || next.isEqual(now)) next = next.plusDays(1);
        long delaySec = Duration.between(now, next).getSeconds();
        System.out.println("Scheduling reminder at " + next.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                " (after " + delaySec + " seconds).");

        scheduler.schedule(() -> triggerReminder(currentPlan), delaySec, TimeUnit.SECONDS);
        System.out.println("Reminder scheduled. (The program must keep running until the reminder appears.)");
    }

    static void triggerReminder(Plan p) {
        String message = "IF-THEN reminder (" + p.scenario + ")\nIf " +
                p.ifThenTime.format(timeFmt) + " then: " + p.ifThenAction +
                "\n\nWorst outcome: " + p.worstOutcome +
                "\nPenalty if not done: " + p.penalty +
                "\nReward after completion: " + p.reward;
        // show Swing dialog on EDT
        SwingUtilities.invokeLater(() -> {
            int res = JOptionPane.showOptionDialog(null, message,
                    "If-Then Reminder",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new String[] { "I did it", "I failed (apply penalty)" },
                    "I did it");
            if (res == 0) {
                JOptionPane.showMessageDialog(null, "Great! Record your progress and claim reward: " + p.reward);
            } else {
                String log = "FAILED at " + LocalDateTime.now() + " | Plan: " + p.scenario + " | Penalty: " + p.penalty;
                writeToFileAppend("penalty_log.txt", log);
                copyToClipboard("I did not do: " + p.scenario + " — Penalty: " + p.penalty);
                JOptionPane.showMessageDialog(null, "Penalty logged and penalty text copied to clipboard.\n(penalty_log.txt)");
            }
        });
        Toolkit.getDefaultToolkit().beep();
    }

    static void startTwoMinuteVisualization() {
        System.out.println("\n--- Start 2-minute visualization + breathing ---");
        System.out.println("1) 60s: Visualize the consequence (who knows, what is lost, when it happens).");
        System.out.println("2) 60s: Write one immediate action (e.g. put on shoes & leave).");
        System.out.println("Starting in 3s...");
        sleepMillis(3000);

        for (int i = 60; i >= 1; i--) {
            System.out.print("\rVisualize: " + i + "s remaining   ");
            sleepMillis(1000);
        }
        System.out.println("\nNow type one immediate action (e.g. 'put on shoes') and press Enter:");
        String action = sc.nextLine().trim();
        System.out.println("Hold for 60s — breathing 4-4-6. (short prompts appear)");
        for (int i = 60; i >= 1; i--) {
            if (i % 10 == 0) {
                System.out.print("\rBreathing: " + i + "s remaining - In 4s, Hold 4s, Out 6s ");
            } else {
                System.out.print("\rBreathing: " + i + "s remaining                            ");
            }
            sleepMillis(1000);
        }
        System.out.println("\nDone. Action recorded: " + action);
        System.out.println("Do you want to set an If-Then reminder now for this action? (y/n)");
        String yn = sc.nextLine().trim().toLowerCase();
        if (yn.equals("y")) {
            System.out.print("Enter time (HH:mm) to remind: ");
            String t = sc.nextLine().trim();
            try {
                currentPlan.ifThenTime = LocalTime.parse(t, timeFmt);
                currentPlan.ifThenAction = action;
                System.out.println("If-Then added to the plan. Use menu 5 to schedule the reminder.");
            } catch (Exception e) {
                System.out.println("Invalid time format.");
            }
        }
    }

    static void copyAccountabilityToClipboard() {
        if (currentPlan == null) {
            System.out.println("No plan.");
            return;
        }
        String msg = currentPlan.makeAccountabilityMessage();
        copyToClipboard(msg);
        System.out.println("Accountability message copied to clipboard. Paste and send to " + currentPlan.accountability);
    }

    static void copyToClipboard(String s) {
        try {
            StringSelection sel = new StringSelection(s);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
        } catch (Exception e) {
            System.out.println("Cannot copy: " + e.getMessage());
        }
    }

    static void writeToFileAppend(String file, String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(content);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    static void sleepMillis(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    // Plan inner class
    static class Plan {
        String scenario = "";
        String worstOutcome = "";
        double costMoney = 0.0;
        double costWeeks = 0.0;
        String psychImpact = "";
        String penalty = "";
        LocalTime ifThenTime = null;
        String ifThenAction = "";
        String accountability = "";
        String reward = "";

        String makeAccountabilityMessage() {
            String a = "I'm about to " + scenario + " in 5 minutes. If I don't reply, please remind me.\n";
            a += "Worst outcome: " + worstOutcome + "\nPenalty (I commit): " + penalty;
            if (!reward.isEmpty()) a += "\nReward if done: " + reward;
            return a;
        }

        String makePenaltyContractText() {
            StringBuilder sb = new StringBuilder();
            sb.append("PENALTY CONTRACT\n");
            sb.append("================\n");
            sb.append("I, [Name], commit to: " + scenario + "\n");
            sb.append("Goal: (Worst realistic outcome if NOT completed):\n");
            sb.append(worstOutcome + "\n\n");
            sb.append("Costs:\n");
            sb.append(String.format(" - Money: %,.0f VND\n", costMoney));
            sb.append(String.format(" - Time: %.1f weeks\n", costWeeks));
            sb.append("Psychological impact: " + psychImpact + "\n\n");
            sb.append("If I fail, I will: " + penalty + "\n");
            if (ifThenTime != null) {
                sb.append("If-Then: If " + ifThenTime.format(timeFmt) + " then " + ifThenAction + "\n");
            }
            if (!accountability.isEmpty()) sb.append("Reminder person: " + accountability + "\n");
            if (!reward.isEmpty()) sb.append("Reward if completed: " + reward + "\n");
            sb.append("\nSignature: ____________________   Date: " + LocalDate.now() + "\n");
            return sb.toString();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Scenario: ").append(scenario).append("\n");
            sb.append("Worst outcome: ").append(worstOutcome).append("\n");
            sb.append(String.format("Money cost: %,.0f VND | Time: %.1f weeks\n", costMoney, costWeeks));
            sb.append("Psychological impact: ").append(psychImpact).append("\n");
            sb.append("Penalty: ").append(penalty).append("\n");
            if (ifThenTime != null) sb.append("If-Then: If ").append(ifThenTime.format(timeFmt)).append(" then ").append(ifThenAction).append("\n");
            sb.append("Accountability: ").append(accountability).append("\n");
            sb.append("Reward: ").append(reward).append("\n");
            return sb.toString();
        }
    }
}
