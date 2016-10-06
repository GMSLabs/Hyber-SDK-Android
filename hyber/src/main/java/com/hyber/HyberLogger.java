package com.hyber;

import android.util.Log;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.unmodifiableList;

public final class HyberLogger {

    /** Log a verbose message with optional format args. */
    public static void v(@NonNls String message, Object... args) {
        TREE_OF_SOULS.v(message, args);
    }

    /** Log an verbose Hyber status. */
    public static void v(HyberStatus status) {
        TREE_OF_SOULS.v(status);
    }

    /** Log a verbose Hyber status and message with optional format args. */
    public static void v(HyberStatus status, @NonNls String message, Object... args) {
        TREE_OF_SOULS.v(status, message, args);
    }

    /** Log a verbose exception and a message with optional format args. */
    public static void v(Throwable t, @NonNls String message, Object... args) {
        TREE_OF_SOULS.v(t, message, args);
    }

    /** Log a verbose exception. */
    public static void v(Throwable t) {
        TREE_OF_SOULS.v(t);
    }

    /** Log a debug message with optional format args. */
    public static void d(@NonNls String message, Object... args) {
        TREE_OF_SOULS.d(message, args);
    }

    /** Log an debug Hyber status. */
    public static void d(HyberStatus status) {
        TREE_OF_SOULS.d(status);
    }

    /** Log a debug Hyber status and message with optional format args. */
    public static void d(HyberStatus status, @NonNls String message, Object... args) {
        TREE_OF_SOULS.d(status, message, args);
    }

    /** Log a debug exception and a message with optional format args. */
    public static void d(Throwable t, @NonNls String message, Object... args) {
        TREE_OF_SOULS.d(t, message, args);
    }

    /** Log a debug exception. */
    public static void d(Throwable t) {
        TREE_OF_SOULS.d(t);
    }

    /** Log an info message with optional format args. */
    public static void i(@NonNls String message, Object... args) {
        TREE_OF_SOULS.i(message, args);
    }

    /** Log an info Hyber status. */
    public static void i(HyberStatus status) {
        TREE_OF_SOULS.i(status);
    }

    /** Log an info Hyber status and message with optional format args. */
    public static void i(HyberStatus status, @NonNls String message, Object... args) {
        TREE_OF_SOULS.i(status, message, args);
    }

    /** Log an info exception and a message with optional format args. */
    public static void i(Throwable t, @NonNls String message, Object... args) {
        TREE_OF_SOULS.i(t, message, args);
    }

    /** Log an info exception. */
    public static void i(Throwable t) {
        TREE_OF_SOULS.i(t);
    }

    /** Log a warning message with optional format args. */
    public static void w(@NonNls String message, Object... args) {
        TREE_OF_SOULS.w(message, args);
    }

    /** Log an warning Hyber status. */
    public static void w(HyberStatus status) {
        TREE_OF_SOULS.w(status);
    }

    /** Log a warning Hyber status and message with optional format args. */
    public static void w(HyberStatus status, @NonNls String message, Object... args) {
        TREE_OF_SOULS.w(status, message, args);
    }

    /** Log a warning exception and a message with optional format args. */
    public static void w(Throwable t, @NonNls String message, Object... args) {
        TREE_OF_SOULS.w(t, message, args);
    }

    /** Log a warning exception. */
    public static void w(Throwable t) {
        TREE_OF_SOULS.w(t);
    }

    /** Log an error message with optional format args. */
    public static void e(@NonNls String message, Object... args) {
        TREE_OF_SOULS.e(message, args);
    }

    /** Log an error Hyber status. */
    public static void e(HyberStatus status) {
        TREE_OF_SOULS.e(status);
    }

    /** Log an error Hyber status and message with optional format args. */
    public static void e(HyberStatus status, @NonNls String message, Object... args) {
        TREE_OF_SOULS.e(status, message, args);
    }

    /** Log an error exception and a message with optional format args. */
    public static void e(Throwable t, @NonNls String message, Object... args) {
        TREE_OF_SOULS.e(t, message, args);
    }

    /** Log an error exception. */
    public static void e(Throwable t) {
        TREE_OF_SOULS.e(t);
    }

    /** Log an assert message with optional format args. */
    public static void wtf(@NonNls String message, Object... args) {
        TREE_OF_SOULS.wtf(message, args);
    }

    /** Log an assert Hyber status. */
    public static void wtf(HyberStatus status) {
        TREE_OF_SOULS.wtf(status);
    }

    /** Log an assert Hyber status and message with optional format args. */
    public static void wtf(HyberStatus status, @NonNls String message, Object... args) {
        TREE_OF_SOULS.wtf(status, message, args);
    }

    /** Log an assert exception and a message with optional format args. */
    public static void wtf(Throwable t, @NonNls String message, Object... args) {
        TREE_OF_SOULS.wtf(t, message, args);
    }

    /** Log an assert exception. */
    public static void wtf(Throwable t) {
        TREE_OF_SOULS.wtf(t);
    }

    /** Log at {@code priority} a message with optional format args. */
    public static void log(int priority, @NonNls String message, Object... args) {
        TREE_OF_SOULS.log(priority, message, args);
    }

    /** Log at {@code priority} a Hyber status. */
    public static void log(int priority, HyberStatus status) {
        TREE_OF_SOULS.log(priority, status);
    }

    /** Log at {@code priority} a Hyber status and message with optional format args. */
    public static void log(int priority, HyberStatus status, @NonNls String message, Object... args) {
        TREE_OF_SOULS.log(priority, status, message, args);
    }

    /** Log at {@code priority} an exception and a message with optional format args. */
    public static void log(int priority, Throwable t, @NonNls String message, Object... args) {
        TREE_OF_SOULS.log(priority, t, message, args);
    }

    /** Log at {@code priority} an exception. */
    public static void log(int priority, Throwable t) {
        TREE_OF_SOULS.log(priority, t);
    }

    /**
     * A view into HyberLogger's planted trees as a tree itself. This can be used for injecting a logger
     * instance rather than using static methods or to facilitate testing.
     */
    public static Tree asTree() {
        return TREE_OF_SOULS;
    }

    /** Set a one-time tag for use on the next logging call. */
    public static Tree tag(String tag) {
        Tree[] forest = forestAsArray;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, count = forest.length; i < count; i++) {
            forest[i].explicitTag.set(tag);
        }
        return TREE_OF_SOULS;
    }

    /** Add a new logging tree. */
    public static void plant(Tree tree) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }
        if (tree == TREE_OF_SOULS) {
            throw new IllegalArgumentException("Cannot plant HyberLogger into itself.");
        }
        synchronized (FOREST) {
            FOREST.add(tree);
            forestAsArray = FOREST.toArray(new Tree[FOREST.size()]);
        }
    }

    /** Adds new logging trees. */
    public static void plant(Tree... trees) {
        if (trees == null) {
            throw new NullPointerException("trees == null");
        }
        for (Tree tree : trees) {
            if (tree == null) {
                throw new NullPointerException("trees contains null");
            }
            if (tree == TREE_OF_SOULS) {
                throw new IllegalArgumentException("Cannot plant HyberLogger into itself.");
            }
        }
        synchronized (FOREST) {
            Collections.addAll(FOREST, trees);
            forestAsArray = FOREST.toArray(new Tree[FOREST.size()]);
        }
    }

    /** Remove a planted tree. */
    public static void uproot(Tree tree) {
        synchronized (FOREST) {
            if (!FOREST.remove(tree)) {
                throw new IllegalArgumentException("Cannot uproot tree which is not planted: " + tree);
            }
            forestAsArray = FOREST.toArray(new Tree[FOREST.size()]);
        }
    }

    /** Remove all planted trees. */
    public static void uprootAll() {
        synchronized (FOREST) {
            FOREST.clear();
            forestAsArray = TREE_ARRAY_EMPTY;
        }
    }

    /** Return a copy of all planted {@linkplain Tree trees}. */
    public static List<Tree> forest() {
        synchronized (FOREST) {
            return unmodifiableList(new ArrayList<>(FOREST));
        }
    }

    public static int treeCount() {
        synchronized (FOREST) {
            return FOREST.size();
        }
    }

    private static final Tree[] TREE_ARRAY_EMPTY = new Tree[0];
    // Both fields guarded by 'FOREST'.
    private static final List<Tree> FOREST = new ArrayList<>();
    private static volatile Tree[] forestAsArray = TREE_ARRAY_EMPTY;

    /** A {@link Tree} that delegates to all planted trees in the {@linkplain #FOREST forest}. */
    private static final Tree TREE_OF_SOULS = new Tree() {
        @Override public void v(String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].v(message, args);
            }
        }

        @Override public void v(HyberStatus status) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].v(status);
            }
        }
        @Override public void v(HyberStatus status, String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].v(status, message, args);
            }
        }

        @Override public void v(Throwable t, String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].v(t, message, args);
            }
        }

        @Override public void v(Throwable t) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].v(t);
            }
        }

        @Override public void d(String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].d(message, args);
            }
        }

        @Override public void d(HyberStatus status) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].d(status);
            }
        }

        @Override public void d(HyberStatus status, String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].d(status, message, args);
            }
        }

        @Override public void d(Throwable t, String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].d(t, message, args);
            }
        }

        @Override public void d(Throwable t) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].d(t);
            }
        }

        @Override public void i(String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].i(message, args);
            }
        }

        @Override public void i(HyberStatus status) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].i(status);
            }
        }

        @Override public void i(HyberStatus status, String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].i(status, message, args);
            }
        }

        @Override public void i(Throwable t, String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].i(t, message, args);
            }
        }

        @Override public void i(Throwable t) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].i(t);
            }
        }

        @Override public void w(String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].w(message, args);
            }
        }

        @Override public void w(HyberStatus status) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].w(status);
            }
        }

        @Override public void w(HyberStatus status, String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].w(status, message, args);
            }
        }

        @Override public void w(Throwable t, String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].w(t, message, args);
            }
        }

        @Override public void w(Throwable t) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].w(t);
            }
        }

        @Override public void e(String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].e(message, args);
            }
        }

        @Override public void e(HyberStatus status) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].e(status);
            }
        }

        @Override public void e(HyberStatus status, String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].e(status, message, args);
            }
        }

        @Override public void e(Throwable t, String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].e(t, message, args);
            }
        }

        @Override public void e(Throwable t) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].e(t);
            }
        }

        @Override public void wtf(String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].wtf(message, args);
            }
        }

        @Override public void wtf(HyberStatus status) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].wtf(status);
            }
        }

        @Override public void wtf(HyberStatus status, String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].wtf(status, message, args);
            }
        }

        @Override public void wtf(Throwable t, String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].wtf(t, message, args);
            }
        }

        @Override public void wtf(Throwable t) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].wtf(t);
            }
        }

        @Override public void log(int priority, String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].log(priority, message, args);
            }
        }

        @Override public void log(int priority, HyberStatus status) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].log(priority, status);
            }
        }

        @Override public void log(int priority, HyberStatus status, String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].log(priority, status, message, args);
            }
        }

        @Override public void log(int priority, Throwable t, String message, Object... args) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].log(priority, t, message, args);
            }
        }

        @Override public void log(int priority, Throwable t) {
            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].log(priority, t);
            }
        }

        @Override protected void log(int priority, String tag, HyberStatus status, String message, Throwable t) {
            throw new AssertionError("Missing override for log method.");
        }
    };

    private HyberLogger() {
        throw new AssertionError("No instances.");
    }

    /** A facade for handling logging calls. Install instances via {@link #plant HyberLogger.plant()}. */
    public abstract static class Tree {
        private final ThreadLocal<String> explicitTag = new ThreadLocal<>();

        String getTag() {
            String tag = explicitTag.get();
            if (tag != null) {
                explicitTag.remove();
            }
            return tag;
        }

        /** Log a verbose message with optional format args. */
        public void v(String message, Object... args) {
            prepareLog(Log.VERBOSE, null, null, message, args);
        }

        /** Log an verbose Hyber status. */
        public void v(HyberStatus status) {
            prepareLog(Log.VERBOSE, null, status, null);
        }

        /** Log a verbose Hyber status and message with optional format args. */
        public void v(HyberStatus status, String message, Object... args) {
            prepareLog(Log.VERBOSE, null, status, message, args);
        }

        /** Log a verbose exception and a message with optional format args. */
        public void v(Throwable t, String message, Object... args) {
            prepareLog(Log.VERBOSE, t, null, message, args);
        }

        /** Log a verbose exception. */
        public void v(Throwable t) {
            prepareLog(Log.VERBOSE, t, null, null);
        }

        /** Log a debug message with optional format args. */
        public void d(String message, Object... args) {
            prepareLog(Log.DEBUG, null, null, message, args);
        }

        /** Log an debug Hyber status. */
        public void d(HyberStatus status) {
            prepareLog(Log.DEBUG, null, status, null);
        }

        /** Log a debug Hyber status and message with optional format args. */
        public void d(HyberStatus status, String message, Object... args) {
            prepareLog(Log.DEBUG, null, status, message, args);
        }

        /** Log a debug exception and a message with optional format args. */
        public void d(Throwable t, String message, Object... args) {
            prepareLog(Log.DEBUG, t, null, message, args);
        }

        /** Log a debug exception. */
        public void d(Throwable t) {
            prepareLog(Log.DEBUG, t, null, null);
        }

        /** Log an info message with optional format args. */
        public void i(String message, Object... args) {
            prepareLog(Log.INFO, null, null, message, args);
        }

        /** Log an info Hyber status. */
        public void i(HyberStatus status) {
            prepareLog(Log.INFO, null, status, null);
        }

        /** Log an info Hyber status and message with optional format args. */
        public void i(HyberStatus status, String message, Object... args) {
            prepareLog(Log.INFO, null, status, message, args);
        }

        /** Log an info exception and a message with optional format args. */
        public void i(Throwable t, String message, Object... args) {
            prepareLog(Log.INFO, t, null, message, args);
        }

        /** Log an info exception. */
        public void i(Throwable t) {
            prepareLog(Log.INFO, t, null, null);
        }

        /** Log a warning message with optional format args. */
        public void w(String message, Object... args) {
            prepareLog(Log.WARN, null, null, message, args);
        }

        /** Log an warning Hyber status. */
        public void w(HyberStatus status) {
            prepareLog(Log.WARN, null, status, null);
        }

        /** Log a warning Hyber status and message with optional format args. */
        public void w(HyberStatus status, String message, Object... args) {
            prepareLog(Log.WARN, null, status, message, args);
        }

        /** Log a warning exception and a message with optional format args. */
        public void w(Throwable t, String message, Object... args) {
            prepareLog(Log.WARN, t, null, message, args);
        }

        /** Log a warning exception. */
        public void w(Throwable t) {
            prepareLog(Log.WARN, t, null, null);
        }

        /** Log an error message with optional format args. */
        public void e(String message, Object... args) {
            prepareLog(Log.ERROR, null, null, message, args);
        }

        /** Log an error Hyber status. */
        public void e(HyberStatus status) {
            prepareLog(Log.ERROR, null, status, null);
        }

        /** Log an error Hyber status and message with optional format args. */
        public void e(HyberStatus status, String message, Object... args) {
            prepareLog(Log.ERROR, null, status, message, args);
        }

        /** Log an error exception and a message with optional format args. */
        public void e(Throwable t, String message, Object... args) {
            prepareLog(Log.ERROR, t, null, message, args);
        }

        /** Log an error exception. */
        public void e(Throwable t) {
            prepareLog(Log.ERROR, t, null, null);
        }

        /** Log an assert message with optional format args. */
        public void wtf(String message, Object... args) {
            prepareLog(Log.ASSERT, null, null, message, args);
        }

        /** Log an assert Hyber status. */
        public void wtf(HyberStatus status) {
            prepareLog(Log.ASSERT, null, status, null);
        }

        /** Log an assert Hyber status and message with optional format args. */
        public void wtf(HyberStatus status, String message, Object... args) {
            prepareLog(Log.ASSERT, null, status, message, args);
        }

        /** Log an assert exception and a message with optional format args. */
        public void wtf(Throwable t, String message, Object... args) {
            prepareLog(Log.ASSERT, t, null, message, args);
        }

        /** Log an assert exception. */
        public void wtf(Throwable t) {
            prepareLog(Log.ASSERT, t, null, null);
        }

        /** Log at {@code priority} a message with optional format args. */
        public void log(int priority, String message, Object... args) {
            prepareLog(priority, null, null, message, args);
        }

        /** Log at {@code priority} a Hyber status . */
        public void log(int priority, HyberStatus status) {
            prepareLog(priority, null, status, null);
        }

        /** Log at {@code priority} a Hyber status and message with optional format args. */
        public void log(int priority, HyberStatus status, String message, Object... args) {
            prepareLog(priority, null, status, message, args);
        }

        /** Log at {@code priority} an exception and a message with optional format args. */
        public void log(int priority, Throwable t, String message, Object... args) {
            prepareLog(priority, t, null, message, args);
        }

        /** Log at {@code priority} an exception. */
        public void log(int priority, Throwable t) {
            prepareLog(priority, t, null, null);
        }

        /**
         * Return whether a message at {@code priority} should be logged.
         * @deprecated use {@link #isLoggable(String, int)} instead.
         */
        @Deprecated
        protected boolean isLoggable(int priority) {
            return true;
        }

        /** Return whether a message at {@code priority} or {@code tag} should be logged. */
        protected boolean isLoggable(String tag, int priority) {
            return isLoggable(priority);
        }

        private void prepareLog(int priority, Throwable t, HyberStatus status, String message, Object... args) {
            // Consume tag even when message is not loggable so that next message is correctly tagged.
            String tag = getTag();

            if (!isLoggable(tag, priority)) {
                return;
            }
            if (message != null && message.length() == 0) {
                message = null;
            }
            if (message == null) {
                if (status != null) {
                    message = String.format(Locale.getDefault(), "%d ==> %s",
                            status.getCode(), status.getDescription());
                } else if (t != null) {
                    message = getStackTraceString(t);
                } else {
                    return; // Swallow message if it's null and there's no throwable.
                }
            } else {
                if (args.length > 0) {
                    message = String.format(message, args);
                }
                if (t != null) {
                    message += "\n" + getStackTraceString(t);
                }
            }

            log(priority, tag, status, message, t);
        }

        private String getStackTraceString(Throwable t) {
            // Don't replace this with Log.getStackTraceString() - it hides
            // UnknownHostException, which is not what we want.
            StringWriter sw = new StringWriter(256);
            PrintWriter pw = new PrintWriter(sw, false);
            t.printStackTrace(pw);
            pw.flush();
            return sw.toString();
        }

        /**
         * Write a log message to its destination. Called for all level-specific methods by default.
         *
         * @param priority Log level. See {@link Log} for constants.
         * @param tag Explicit or inferred tag. May be {@code null}.
         * @param status Object Hyber status. May be {@code null}, but then {@code t} or {@code message} will not be.
         * @param message Formatted log message. May be {@code null}, but then {@code t} will not be.
         * @param t Accompanying exceptions. May be {@code null}, but then {@code message} or {@code status} will not be.
         */
        protected abstract void log(int priority, @Nullable String tag, @Nullable HyberStatus status,
                                    @Nullable String message, @Nullable Throwable t);
    }

    /** A {@link Tree Tree} for debug builds. Automatically infers the tag from the calling class. */
    public static class DebugTree extends Tree {
        private static final int MAX_LOG_LENGTH = 4000;
        private static final int CALL_STACK_INDEX = 5;
        private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");

        /**
         * Extract the tag which should be used for the message from the {@code element}. By default
         * this will use the class name without any anonymous class suffixes (e.g., {@code Foo$1}
         * becomes {@code Foo}).
         * <p>
         * Note: This will not be called if a {@linkplain #tag(String) manual tag} was specified.
         */
        protected String createStackElementTag(StackTraceElement element) {
            String tag = element.getClassName();
            Matcher m = ANONYMOUS_CLASS.matcher(tag);
            if (m.find()) {
                tag = m.replaceAll("");
            }
            return tag.substring(tag.lastIndexOf('.') + 1);
        }

        @Override final String getTag() {
            String tag = super.getTag();
            if (tag != null) {
                return tag;
            }

            // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
            // because Robolectric runs them on the JVM but on Android the elements are different.
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            if (stackTrace.length <= CALL_STACK_INDEX) {
                throw new IllegalStateException(
                        "Synthetic stacktrace didn't have enough elements: are you using proguard?");
            }
            return createStackElementTag(stackTrace[CALL_STACK_INDEX]);
        }

        /**
         * Break up {@code message} into maximum-length chunks (if needed) and send to either
         * {@link Log#println(int, String, String) Log.println()} or
         * {@link Log#wtf(String, String) Log.wtf()} for logging.
         *
         * {@inheritDoc}
         */
        @Override protected void log(int priority, String tag, HyberStatus status, String message, Throwable t) {
            if (message.length() < MAX_LOG_LENGTH) {
                if (priority == Log.ASSERT) {
                    Log.wtf(tag, message);
                } else {
                    Log.println(priority, tag, message);
                }
                return;
            }

            // Split by line, then ensure each line can fit into Log's maximum length.
            for (int i = 0, length = message.length(); i < length; i++) {
                int newline = message.indexOf('\n', i);
                newline = newline != -1 ? newline : length;
                do {
                    int end = Math.min(newline, i + MAX_LOG_LENGTH);
                    String part = message.substring(i, end);
                    if (priority == Log.ASSERT) {
                        Log.wtf(tag, part);
                    } else {
                        Log.println(priority, tag, part);
                    }
                    i = end;
                } while (i < newline);
            }
        }
    }

}
