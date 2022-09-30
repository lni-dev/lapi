/*
 * Copyright (c) 2022 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.linusdev.lapi.api.communication.http.queue;

import me.linusdev.lapi.api.communication.http.ratelimit.Bucket;
import me.linusdev.lapi.api.communication.http.ratelimit.RateLimitId;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BucketDebugger extends JFrame {

    private final @NotNull ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final @NotNull Map<RateLimitId, Bucket> buckets;
    private final @NotNull Bucket global;
    private final @NotNull BucketView globalView;

    private final Map<Bucket, BucketView> views;

    private final @NotNull JLabel top;
    private final @NotNull AtomicInteger updates = new AtomicInteger(0);

    public BucketDebugger(@NotNull Map<RateLimitId, Bucket> buckets, @NotNull Bucket global) {
        super("Rate Limit Buckets");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.buckets = buckets;
        this.global = global;
        this.globalView = new BucketView(global);
        this.views = new HashMap<>();
        this.top = new JLabel("0");
        init();
    }

    private void init() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 1000, 10));
        getContentPane().add(top);
        getContentPane().add(globalView);
        for(Map.Entry<RateLimitId, Bucket> entry : buckets.entrySet()) {
            BucketView v = new BucketView(entry.getValue());
            views.put(entry.getValue(), v);
            getContentPane().add(v);
        }

        pack();
        setSize(1000, 500);
        setVisible(true);

        executor.scheduleAtFixedRate(this::update, 100, 100, TimeUnit.MILLISECONDS);
    }

    private void update() {
        top.setText(String.valueOf(updates.incrementAndGet()));

        for(Map.Entry<RateLimitId, Bucket> entry : buckets.entrySet()) {
            if(!views.containsKey(entry.getValue())){
                BucketView v = new BucketView(entry.getValue());
                views.put(entry.getValue(), v);
                getContentPane().add(v);
            }

        }

        globalView.refresh();
        for(BucketView v : views.values()) {
            v.refresh();
        }

        repaint();
    }

    private static class BucketView extends JLabel {
        private final @NotNull Bucket bucket;

        private BucketView(@NotNull Bucket bucket) {
            super(bucket.toString());
            this.bucket = bucket;
        }

        public void refresh() {
            setText(bucket.toString());

            if(bucket.getQueueSize() > 0) {
                setBorder(new ColorBorder(Color.red));
            } else {
                setBorder(new ColorBorder(Color.white));
            }

            if(bucket.isDeleted()) {
                setIcon(new ColorIcon(Color.red, getSize().height-1));

            } else if(bucket.isAssumed()){
                setIcon(new ColorIcon(Color.orange, getSize().height-1));

            } else {
                setIcon(new ColorIcon(Color.green, getSize().height-1));

            }
        }
    }

    private static class ColorIcon implements Icon {

        private final Color color;
        private final int size;

        private ColorIcon(Color color, int size) {
            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            graphics.setColor(color);
            graphics.fillRect(x, y, size, size);
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    private static class ColorBorder implements Border {

        private final Color color;

        private ColorBorder(Color color) {
            this.color = color;
        }

        @Override
        public void paintBorder(Component component, Graphics graphics, int x, int y, int w, int h) {
            graphics.setColor(color);
            graphics.drawRect(x, y, w-1, h-1);

        }

        @Override
        public Insets getBorderInsets(Component component) {
            return new Insets(0, 0, 0, 0);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
