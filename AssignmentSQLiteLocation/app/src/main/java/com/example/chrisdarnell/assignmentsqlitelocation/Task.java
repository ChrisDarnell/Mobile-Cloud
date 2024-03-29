package com.example.chrisdarnell.assignmentsqlitelocation;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by chrisdarnell on 7/30/17.
 */


    @Entity
    public class Task {

        @PrimaryKey
        public final long id;
        public String summary;
        public String description;
        public boolean done;
//    public Date dueDate;


        public Task(long id, String summary, String description, boolean done) {
            this.id = id;
            this.summary = summary;
            this.description = description;
            this.done = done;
//        this.dueDate = dueDate;
        }

        public static TaskBuilder builder(){
            return new TaskBuilder();
        }

        public static class TaskBuilder {
            private long id;
            private String summary = "";
            private String description = "";
            private boolean done = false;

            public TaskBuilder setId(long id) {
                this.id = id;
                return this;
            }

            public TaskBuilder setSummary(String summary) {
                this.summary = summary;
                return this;
            }

            public TaskBuilder setDescription(String description) {
                this.description = description;
                return this;
            }

            public TaskBuilder setDone(boolean done) {
                this.done = done;
                return this;
            }



            public Task build() {
                return new Task(id, summary, description, done);
            }
        }

        @Override
        public String toString() {
            return "Task{" +
                    "id=" + id +
                    ", summary='" + summary + '\'' +
                    ", description='" + description + '\'' +
                    ", done=" + done +
                    '}';
        }
    }

