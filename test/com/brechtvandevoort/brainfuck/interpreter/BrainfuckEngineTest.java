package com.brechtvandevoort.brainfuck.interpreter;

import com.brechtvandevoort.brainfuck.programs.BrainfuckPrograms;
import junit.framework.TestCase;

/**
 * Created by brecht on 28/03/2016.
 */
public class BrainfuckEngineTest extends TestCase {

    public void testExecute() throws Exception {
        BrainfuckEngine engine = new BrainfuckEngine(BrainfuckPrograms.BRAINFUCK_PROGRAM_HELLO_WORLD);

        engine.execute();

        BrainfuckInstance instance = engine.getBrainfuckInstance();
        String output = "";
        while (instance.hasOutput()) {
            output += (char)instance.readFromOutputStream();
        }
        assertEquals("Hello World!\n", output);
    }
}