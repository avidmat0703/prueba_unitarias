package org.iesvdm.tddjava.connect4;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class Connect4TDDSpec {

    private Connect4TDD tested;

    private OutputStream output;

    @BeforeEach
    public void beforeEachTest() {
        output = new ByteArrayOutputStream();

        //Se instancia el juego modificado para acceder a la salida de consola
        tested = new Connect4TDD(new PrintStream(output));
    }

    /*
     * The board is composed by 7 horizontal and 6 vertical empty positions
     */

    @Test
    public void whenTheGameStartsTheBoardIsEmpty() {
        assertThat(tested.getNumberOfDiscs()).isEqualTo(0);
    }

    /*
     * Players introduce discs on the top of the columns.
     * Introduced disc drops down the board if the column is empty.
     * Future discs introduced in the same column will stack over previous ones
     */

    @Test
    public void whenDiscOutsideBoardThenRuntimeException() {
        assertThrows(RuntimeException.class, () -> {
            tested.putDiscInColumn(10);
        });
    }

    @Test
    public void whenFirstDiscInsertedInColumnThenPositionIsZero() {
        assertThat(tested.putDiscInColumn(1)).isEqualTo(0);
    }

    @Test
    public void whenSecondDiscInsertedInColumnThenPositionIsOne() {
        tested.putDiscInColumn(1);
        assertThat(tested.putDiscInColumn(1)).isEqualTo(1);
    }

    @Test
    public void whenDiscInsertedThenNumberOfDiscsIncreases() {
        int n = tested.getNumberOfDiscs();
        tested.putDiscInColumn(1);
        assertThat(tested.getNumberOfDiscs()).isEqualTo(n+1);
    }

    @Test
    public void whenNoMoreRoomInColumnThenRuntimeException() {
            assertThrows(RuntimeException.class, () -> {
            for (int i = 0; i < 7; i++) {
                tested.putDiscInColumn(0);
            }
        });
    }

    /*
     * It is a two-person game so there is one colour for each player.
     * One player uses red ('R'), the other one uses green ('G').
     * Players alternate turns, inserting one disc every time
     */

    @Test
    public void whenFirstPlayerPlaysThenDiscColorIsRed() {
        assertThat(tested.getCurrentPlayer()).isEqualTo("R");
    }

    @Test
    public void whenSecondPlayerPlaysThenDiscColorIsGreen() {
        assertThat(tested.putDiscInColumn(1)).isEqualTo(0);
        assertThat(tested.getCurrentPlayer()).isEqualTo("G");
    }

    /*
     * We want feedback when either, event or error occur within the game.
     * The output shows the status of the board on every move
     */

    @Test
    public void whenAskedForCurrentPlayerTheOutputNotice() {
        tested.getCurrentPlayer();
        assertThat(output.toString()).isEqualTo("Player R turn\r\n");
        //Quitar el \r si se hace en linux para que pase correctamente.
    }

    @Test
    public void whenADiscIsIntroducedTheBoardIsPrinted() {
        tested.putDiscInColumn(0);
        String expectedOutput = "| | | | | | | |\r\n" +
                "| | | | | | | |\r\n" +
                "| | | | | | | |\r\n" +
                "| | | | | | | |\r\n" +
                "| | | | | | | |\r\n" +
                "|R| | | | | | |\r\n";
        assertThat(output.toString()).isEqualTo(expectedOutput);
        //Quitar el \r si se hace en linux para que pase correctamente.
    }

    /*
     * When no more discs can be inserted, the game finishes and it is considered a draw
     */

    @Test
    public void whenTheGameStartsItIsNotFinished() {
        assertThat(tested.isFinished()).isEqualTo(false);
    }

    @Test
    public void whenNoDiscCanBeIntroducedTheGamesIsFinished() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                tested.putDiscInColumn(i);
            }
        }
        assertThat(tested.isFinished()).isEqualTo(true);
    }

    /*
     * If a player inserts a disc and connects more than 3 discs of his colour
     * in a straight vertical line then that player wins
     */

    @Test
    public void when4VerticalDiscsAreConnectedThenThatPlayerWins() {
        for (int i = 0; i < 3; i++) {
            tested.putDiscInColumn(0);
            tested.putDiscInColumn(1);
        }
        tested.putDiscInColumn(0);
        assertThat(tested.getWinner()).isEqualTo("R");
    }

    /*
     * If a player inserts a disc and connects more than 3 discs of his colour
     * in a straight horizontal line then that player wins
     */

    @Test
    public void when4HorizontalDiscsAreConnectedThenThatPlayerWins() {
        for (int i = 0; i < 3; i++) {
            tested.putDiscInColumn(i);
            tested.putDiscInColumn(i);
        }
        tested.putDiscInColumn(3);
        assertThat(tested.getWinner()).isEqualTo("R");
    }

    /*
     * If a player inserts a disc and connects more than 3 discs of his colour
     * in a straight diagonal line then that player wins
     */

    @Test
    public void when4Diagonal1DiscsAreConnectedThenThatPlayerWins() {
        tested.putDiscInColumn(0);//La primera diagonal
        tested.putDiscInColumn(1);
        tested.putDiscInColumn(1);//La segunda diagonal
        tested.putDiscInColumn(2);
        tested.putDiscInColumn(2);
        tested.putDiscInColumn(3);
        tested.putDiscInColumn(2);//La tercera diagonal
        tested.putDiscInColumn(3);
        tested.putDiscInColumn(3);
        tested.putDiscInColumn(5);
        tested.putDiscInColumn(3);//La cuarta diagonal
        assertThat(tested.getWinner()).isEqualTo("R");
    }

    @Test
    public void when4Diagonal2DiscsAreConnectedThenThatPlayerWins() {
        tested.putDiscInColumn(3);//La primera diagonal
        tested.putDiscInColumn(2);
        tested.putDiscInColumn(2);//La segunda diagonal
        tested.putDiscInColumn(1);
        tested.putDiscInColumn(1);
        tested.putDiscInColumn(6);
        tested.putDiscInColumn(1);//La tercera diagonal
        tested.putDiscInColumn(0);
        tested.putDiscInColumn(0);
        tested.putDiscInColumn(0);
        tested.putDiscInColumn(0);//La cuarta diagonal
        assertThat(tested.getWinner()).isEqualTo("R");
    }
}