package com.brechtvandevoort.brainfuck.interpreter;


/**
 * Created by brecht on 24/03/2016.
 */
public class BrainfuckEngine {
    private BrainfuckInstance _instance;
    private String _instructions;
    private int _instructionPointer;

    private int _executionInterval;

    public BrainfuckEngine(BrainfuckInstance instance, String instructions) {
        _instance = instance;
        _instructions = instructions;
        _instructionPointer = 0;
    }

    public BrainfuckEngine(String instructions) {
        _instance = new BrainfuckInstance();
        _instructions = instructions;
        _instructionPointer = 0;
        _executionInterval = 0;
    }

    public BrainfuckInstance getBrainfuckInstance() {
        return _instance;
    }

    public int getInstructionPointerPosition() {
        return _instructionPointer;
    }

    public void execute() {
        while(_instructionPointer < _instructions.length()) {
            executeStep();
            if (_executionInterval > 0) {
                try {
                    Thread.sleep(_executionInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean finished() {
        return _instructionPointer < 0 || _instructionPointer >= _instructions.length();
    }

    public void setExecutionInterval(int ms) {
        _executionInterval = ms;
    }

    public void executeStep() {
        if(finished())
            return;

        executeInstruction();

        _instructionPointer++;
    }

    public String getInstructions() {
        return _instructions;
    }

    private void executeInstruction() {
        switch (_instructions.charAt(_instructionPointer)) {
            case '<':
                _instance.decrementPointer();
                break;
            case '>':
                _instance.incrementPointer();
                break;
            case '+':
                _instance.incrementValue();
                break;
            case '-':
                _instance.decrementValue();
                break;
            case '.':
                _instance.writeOutput();
                break;
            case ',':
                _instance.readInput();
                break;
            case '[':
                executeJumpForward();
                break;
            case ']':
                executeJumpBack();
                break;
            default:
                break;
        }
    }

    private void executeJumpForward() {
        if(_instance.getValue() == 0) {
            int pos = _instructions.indexOf(']', _instructionPointer);
            if(pos >= 0) {
                _instructionPointer = pos;
            }
        }
    }

    private void executeJumpBack() {
        if(_instance.getValue() != 0) {
            int pos = _instructions.substring(0, _instructionPointer).lastIndexOf('[');
            if (pos >= 0) {
                _instructionPointer = pos;
            }
        }
    }
}
