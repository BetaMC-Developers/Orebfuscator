package lishid.orebfuscator.utils;

import net.minecraft.server.Packet51MapChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.zip.Deflater;

public class OrebfuscatorCalculationThread extends Thread implements Runnable {
    private boolean kill = false;
    //Global
    private static final int QUEUE_CAPACITY = 1024 * 10;
    private static final ArrayList<OrebfuscatorCalculationThread> threads = new ArrayList<>();
    private static final LinkedBlockingDeque<ObfuscatedPlayerPacket> queue = new LinkedBlockingDeque<>(QUEUE_CAPACITY);
    public final Deflater deflater = new Deflater();
    public byte[] deflateBuffer = new byte[82020];

    public static int getThreads() {
        return threads.size();
    }

    public static boolean CheckThreads() {
        return threads.size() == OrebfuscatorConfig.ProcessingThreads();
    }

    //Get Queue Size
    public static int getQueueSize() {
        return queue.size();
    }

    //Get number of running threads
    public static int getRunningThreads() {
        int running = 0;
        for (OrebfuscatorCalculationThread thread : threads) {
            if (thread.isAlive()) {
                running++;
            }
        }
        return running;
    }

    public static void SyncThreads() {
        if (threads.size() == OrebfuscatorConfig.ProcessingThreads())
            return;
        int runningThreads = 0;
        for (OrebfuscatorCalculationThread thread : threads) {
            if (thread.isAlive() && !thread.isInterrupted() && !thread.kill) {
                runningThreads = runningThreads + 1;
            } else {
                System.out.println("A random semidead thread has been found, code: jKis89");
            }
        }

        int extra = runningThreads - OrebfuscatorConfig.ProcessingThreads();
        if (extra > 0) {
            for (int i = extra; i > 0; i--) {
                threads.get(i - 1).kill = true;
                threads.remove(i - 1);
            }
        } else if (extra < 0) {
            for (int i = 0; i < -extra; i++) {
                OrebfuscatorCalculationThread thread = new OrebfuscatorCalculationThread();
                thread.start();
                thread.setName("Orebfuscator Calculation Thread");
                threads.add(thread);
            }
        }
    }

    public void run() {
        while (!this.isInterrupted() && !kill) {
            try {
                handle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handle() {
        try {
            ObfuscatedPlayerPacket packet = queue.take();
            Calculations.Obfuscate(this, packet.packet, packet.player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Queue(Packet51MapChunk packet, CraftPlayer player) {
        while (true) {
            try {
                queue.put(new ObfuscatedPlayerPacket(player, packet));
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
