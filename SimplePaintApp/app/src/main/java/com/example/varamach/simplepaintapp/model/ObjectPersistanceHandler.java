package com.example.varamach.simplepaintapp.model;

import android.app.Activity;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by varamach on 2/23/18.
 * Handler that writes the serializable object to a file and reads back.
 * TODO:Save operation is done a new thread. Read operation is not. Since it is a 2 day project,
 * I didn't have time to complete and test it.
 * UI needs to handle the async read operation.
 * Reused the code from https://stackoverflow.com/questions/15841997/saving-parcelable-data
 */
class ObjectPersistanceHandler {


        private final Context mContext;
        private final Thread worker;
        private final WriteObjectToFile writer;

        public ObjectPersistanceHandler(Context context) {
            mContext = context;

            // Construct a writer to hold and save the data
            writer = new WriteObjectToFile();

            // Construct a worker thread to handle the writer
            worker = new Thread(writer);

        }


        // Method to save the data
        public void saveObjectData(Object object, String key) {

            if (object != null){
                // Write the data to disc
                writer.setParams(new WriteParams(object, key));
                worker.run();
            }
        }


        // Method to read the Global Data
        public Object readObjectData(String key){

            Object returnData = readObjectFromFile(key);
            return returnData;
        }


        // Method to erase the data
        public void clearObjectData(String key){

            writer.setParams(new WriteParams(null, key));
            worker.run();

        }

        private class WriteObjectToFile implements Runnable {

            WriteParams params;

            public void setParams(WriteParams params) {
                this.params = params;
            }

            public void run() {
                writeObjectToFile(params.getObject(), params.getFilename());
            }

            private boolean writeObjectToFile(Object object, String filename) {

                boolean success = true;

                ObjectOutputStream objectOut = null;
                try {

                    FileOutputStream fileOut = mContext.openFileOutput(filename, Activity.MODE_PRIVATE);
                    objectOut = new ObjectOutputStream(fileOut);
                    objectOut.writeObject(object);
                    fileOut.getFD().sync();

                } catch (IOException e) {
                    success = false;
                    e.printStackTrace();
                } finally {
                    if (objectOut != null) {
                        try {
                            objectOut.close();
                        } catch (IOException e) {
                        }

                    }
                }
                return success;
            }
        }


        private Object readObjectFromFile(String filename) {

            ObjectInputStream objectIn = null;
            Object object = null;
            try {

                FileInputStream fileIn = mContext.getApplicationContext().openFileInput(filename);
                objectIn = new ObjectInputStream(fileIn);
                object = objectIn.readObject();

            } catch (FileNotFoundException e) {
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (objectIn != null) {
                    try {
                        objectIn.close();
                    } catch (IOException e) {
                    }
                }
            }

            return object;
        }


        private static class WriteParams {

            final Object object;
            final String filename;

            public WriteParams(Object object, String filename) {
                super();
                this.object = object;
                this.filename = filename;
            }

            public Object getObject() {
                return object;
            }

            public String getFilename() {
                return filename;
            }

        }

}
