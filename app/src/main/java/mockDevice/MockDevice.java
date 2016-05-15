package mockDevice;

import android.bluetooth.BluetoothDevice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by arpit on 15/5/16.
 */
public class MockDevice {

    private BluetoothDevice device;
    int[][] dataSource = new int[100][3];
    StringBuilder stringData = new StringBuilder();

    /**
     * Data Format that app is expecting from arduino
     * circuit is
     * reader.readLine() -> load, displacement, time
     */
    public MockDevice() {
        int vals = 0;
       /* for(int row = 0; row < dataSource.length; row++) {
            for (int col = 0; col < dataSource[row].length; col++)
                dataSource[row][col]=vals;
            vals++;
        }*/

        for(int i = 0; i < 100; i++){
            for(int j = 0; j < 3; j++) {
                if(j == 2) stringData.append(String.valueOf(vals));
                else stringData.append(String.valueOf(vals) + ",");
            }
            stringData.append("\n");
            vals++;
        }
    }

    public OutputStream getOutputStream(){
        return new MyOutputStream();
    }

    public InputStream getInputStream(){
        return new MyInputStream(stringData.toString());
    }

    private class MyOutputStream extends OutputStream{
        @Override
        public void write(int i) throws IOException {}
    }

    private class MyInputStream extends InputStream {
        private String stringData;
        private int offset = 0;
        public MyInputStream(String stringData) {
            super();
            this.stringData = stringData;
        }

        /**
         * will return string char by char until the end of
         * stringdata, then it will return newLine;
         *
         * Sleeping 500 ms to provide actual data trasmission feel
         * @return
         * @throws IOException
         */
        @Override
        public int read() throws IOException {
            char c = '1';
            if(offset >= stringData.length()) offset = 0;

            if(offset < stringData.length())
                c = stringData.charAt(offset++);

            return c;
        }

    }

    public static void main(String[] args) throws IOException {
        System.out.println("Inside Android Studio");
        MockDevice device = new MockDevice();
        System.out.println(device.stringData);

        InputStream in = device.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while((line = reader.readLine()) != null)
            System.out.println("Data : " + line);
    }
}
