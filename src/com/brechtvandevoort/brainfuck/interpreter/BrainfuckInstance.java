package com.brechtvandevoort.brainfuck.interpreter;

import java.util.Observable;
import java.util.concurrent.*;

/**
 * Data instance of a Brainfuck program.
 * It contains the value of each cell and the current position of the pointer.
 *
 * Created by brecht on 23/03/2016.
 */
public class BrainfuckInstance extends Observable {
    private static int DATA_BYTES_SIZE = 30000;

    private byte _bytes[];
    private int _pointer;
    private int _usedRange;

    private BlockingQueue<Byte> _inputStream;
    private BlockingQueue<Byte> _outputStream;
    private String _completeOutput;

    public BrainfuckInstance() {
        _bytes = new byte[DATA_BYTES_SIZE];
        _pointer = 0;
        _usedRange = 0;
        _inputStream = new LinkedBlockingQueue<>();
        _outputStream = new LinkedBlockingQueue<>();
        _completeOutput = "";
    }

    public void incrementPointer() {
        _pointer++;
        _usedRange = (_usedRange < _pointer)? _pointer : _usedRange;
        setChanged();
        notifyObservers();
    }

    public void decrementPointer() {
        _pointer--;
        setChanged();
        notifyObservers();
    }

    public void incrementValue() {
        _bytes[_pointer]++;
        setChanged();
        notifyObservers();
    }

    public void decrementValue() {
        _bytes[_pointer]--;
        setChanged();
        notifyObservers();
    }

    public byte getValue() {
        return _bytes[_pointer];
    }

    public byte getValueAt(int position) {
        return _bytes[position];
    }

    public int getPointerPosition() {
        return _pointer;
    }

    public int getUsedRange() {
        return _usedRange;
    }

    public byte readFromOutputStream() {
        try {
            return _outputStream.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean hasOutput() {
        return _outputStream.size() > 0;
    }

    public String emptyOutputStream() {
        String output = "";
        while(hasOutput()) {
            output += (char) readFromOutputStream();
        }
        return output;
    }

    public void writeToInputStream(byte b) {
        try {
            _inputStream.put(b);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void readInput() {
        try {
            _bytes[_pointer] = _inputStream.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setChanged();
        notifyObservers();
    }

    public void writeOutput() {
        try {
            _outputStream.put(_bytes[_pointer]);
            _completeOutput += (char)_bytes[_pointer];
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setChanged();
        notifyObservers();
    }

    public String getCompleteOutput() {
        return _completeOutput;
    }
}
