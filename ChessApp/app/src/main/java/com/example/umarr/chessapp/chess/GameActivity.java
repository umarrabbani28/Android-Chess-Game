package com.example.umarr.chessapp.chess;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.umarr.chessapp.R;
import com.example.umarr.chessapp.pieces.Bishop;
import com.example.umarr.chessapp.pieces.King;
import com.example.umarr.chessapp.pieces.Knight;
import com.example.umarr.chessapp.pieces.Pawn;
import com.example.umarr.chessapp.pieces.Piece;
import com.example.umarr.chessapp.pieces.Queen;
import com.example.umarr.chessapp.pieces.Rook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.out;

public class GameActivity extends AppCompatActivity {

    /** the main game board*/
    public static Piece[][] board;

    /** identifies if the game is over */
    public static boolean gameOver = false;

    /** identifies who's turn it is */
    public static boolean isWhiteTurn = false;

    /** identifies if a draw is requested by the opponent*/
    public static boolean drawRequested = false;
    int drawRequestedNum = 0;
    boolean requestJustEnded = false;

    /** identifies if the black king is in check */
    public static boolean blackChecked = false;

    /** identifies if the white king is in check */
    public static boolean whiteChecked = false;

    /** identifies if a pawn has just moved 2 places in one turn */
    public static int pawnMovedDouble = 0;

    /** identifies the pawn the is taken when doing en passant */
    public static Piece enPassantPawn = null;

    boolean tester = false;

    boolean validMove = false;

    boolean firstPieceSet = false;
    boolean secondPieceSet = false;
    String firstPiece;
    String secondPiece;

    Context currContext = this;

    Button resignButton;
    Button moveButton;
    Button drawButton;
    Button homeButton;
    Button helpButton;
    Button undoButton;
    Button saveGameButton;

    TextView winnerText;
    TextView checkMateText;
    TextView drawText;
    TextView drawNotification;
    TextView turnText;

    String promoteTo;

    // used for undo
    public static Piece[][] undoBoard = new Piece[8][8];
    boolean undoWhiteChecked;
    boolean undoBlackChecked;
    int undoPawnMovedDouble;
    Piece undoEnPassantPawn;
    boolean undoIsWhiteTurn;
    int undoDrawRequestedNum;

    // used for saving games
    List<String> savedInstructions = new ArrayList<>();
    String saveName;


    final String[] letterConversion = {"a","b","c","d","e","f","g","h"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);



        AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
        builder.setTitle("How to play");
        builder.setMessage("To make a move, tap a piece, then the spot you want to move to, then press move. Click help to have the computer make the move for you.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

        MainActivity.progressBar.setVisibility(View.GONE);


        isWhiteTurn = true;
        drawRequested = false;

        resignButton = (Button)findViewById(R.id.resignButton);
        moveButton = (Button)findViewById(R.id.moveButton);
        drawButton = (Button)findViewById(R.id.drawButton);
        homeButton = (Button)findViewById(R.id.homeButton);
        helpButton = (Button)findViewById(R.id.helpButton);
        undoButton = (Button)findViewById(R.id.undoButton);
        saveGameButton = (Button)findViewById(R.id.saveGameButton);

        undoButton.setEnabled(false);
        saveGameButton.setVisibility(View.INVISIBLE);

        winnerText = (TextView)findViewById(R.id.winnerText);
        checkMateText = (TextView)findViewById(R.id.checkMateText);
        drawText = (TextView)findViewById(R.id.drawText);
        drawNotification = (TextView)findViewById(R.id.drawNotification);
        turnText = (TextView)findViewById(R.id.turnText);

        homeButton.setVisibility(View.INVISIBLE);
        winnerText.setVisibility(View.INVISIBLE);
        checkMateText.setVisibility(View.INVISIBLE);
        drawText.setVisibility(View.INVISIBLE);
        drawNotification.setVisibility(View.INVISIBLE);

        initializeBoard();

    //    drawBoard();
        drawActualBoard();
        moveButton.setEnabled(false);

        helpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (isWhiteTurn){
                    computerMove("white");
                } else {
                    computerMove("black");
                }
                undoButton.setEnabled(true);
            }
        });

        undoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){



                if (undoBoard != null)
                    out.println("TEST4: undoboard[0][2] = "+undoBoard[0][2]);
                for (int i=0;i<=7;i++){
                    for (int j=0;j<=7;j++){
                        board[i][j] = undoBoard[i][j];
                    }
                }
                whiteChecked = undoWhiteChecked;
                blackChecked = undoBlackChecked;
                enPassantPawn = undoEnPassantPawn;
                drawRequestedNum = undoDrawRequestedNum;
                pawnMovedDouble = undoPawnMovedDouble;
                isWhiteTurn = undoIsWhiteTurn;

                if (drawRequestedNum == 0) {
                    drawText.setVisibility(View.INVISIBLE);
                } else {
                    drawText.setVisibility(View.VISIBLE);
                }

                if (isWhiteTurn){
                    if (whiteChecked)
                        turnText.setText("White's turn: CHECK!");
                    else
                        turnText.setText("White's turn");
                } else {
                    if (blackChecked){
                        turnText.setText("Black's turn: CHECK!");
                    } else
                        turnText.setText("Black's turn");
                }

                drawActualBoard();
                view.setEnabled(false);
                firstPieceSet = false;
                secondPieceSet = false;
                moveButton.setEnabled(false);
            }
        });

        drawButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(currContext);

                if (drawRequestedNum==0){
                    builder.setTitle("Confirm draw request");
                    builder.setMessage("Are you sure you want to request a draw?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            drawRequestedNum = 2;
                            drawNotification.setVisibility(View.VISIBLE);
                            if (isWhiteTurn){
                                savedInstructions.add("draw requested white");
                            } else
                                savedInstructions.add("draw requested black");
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                } else {
                    builder.setTitle("Confirm draw accept");
                    builder.setMessage("Are you sure you want to accept the draw request?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            drawText.setVisibility(View.INVISIBLE);
                            if (isWhiteTurn){
                                savedInstructions.add("draw accepted white");
                            } else
                                savedInstructions.add("draw accepted black");
                            endGame("draw",false);
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            }
        });

        resignButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
                builder.setTitle("Confirm resignation");
                builder.setMessage("Are you sure you want to resign?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isWhiteTurn) {
                            savedInstructions.add("resign white");
                            endGame("Black", false);
                        }else {
                            savedInstructions.add("resign black");
                            endGame("White", false);
                        }
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        moveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (firstPieceSet && secondPieceSet){
                    String instruction = firstPiece + " " + secondPiece;

                    if (isWhiteTurn) {
                        if (whiteChecked) {
                            out.println("Check");
                            out.println();
                        }
                        out.print("White's move: ");
                    }else {
                        if (blackChecked) {
                            out.println("Check");
                            out.println();
                        }
                        out.print("Black's move: ");
                    }

                    validMove = executeInstruction(instruction);

                    if (validMove) {
                        if (isWhiteTurn)
                            whiteChecked = false;
                        else
                            blackChecked = false;

                        // if opponent's king is in check
                        String checkColor;
                        if (isWhiteTurn)
                            checkColor = "black";
                        else
                            checkColor = "white";

                        if (kingCheck(checkColor)) {
                            // checks for checkmate

                            if (!noLegalMoves(checkColor)) {
                                if (isWhiteTurn)
                                    blackChecked = true;
                                else
                                    whiteChecked = true;
                            } else {
                                // game is over
                                drawActualBoard();;
                                if (isWhiteTurn) {
                                    savedInstructions.add("checkmate white");
                                    endGame("White", true);
                                }else {
                                    savedInstructions.add("checkmate black");
                                    endGame("Black", true);
                                }
                                /*gameOver = true;
                                System.out.println("Checkmate");
                                System.out.println();
                                if (isWhiteTurn) {
                                    System.out.println("White wins");
                                } else {
                                    System.out.println("Black wins");
                                }*/
                            }
                        } else {
                            // no moves left for opponent but king isn't in check (stalemate)
                            if (noLegalMoves(checkColor)) {
                                drawActualBoard();
                                out.println("Stalemate");
                                out.println();
                                out.println("draw");
                                gameOver = true;
                                savedInstructions.add("stalemate");
                                endGame("stalemate",false);
                            }
                        }

                        drawActualBoard();
                        firstPieceSet = false;
                        secondPieceSet = false;
                        isWhiteTurn = !isWhiteTurn;
                        view.setEnabled(false);

                        undoButton.setEnabled(true);

                        if (isWhiteTurn){
                            if (whiteChecked)
                                turnText.setText("White's turn: CHECK!");
                            else
                                turnText.setText("White's turn");
                        } else {
                            if (blackChecked){
                                turnText.setText("Black's turn: CHECK!");
                            } else
                                turnText.setText("Black's turn");
                        }

                        if (drawRequestedNum > 1){
                            drawText.setVisibility(View.VISIBLE);
                            drawNotification.setVisibility(View.INVISIBLE);
                            drawRequestedNum--;
                            undoDrawRequestedNum = 0;
                        } else if (drawRequestedNum == 1){
                            drawRequestedNum--;
                            drawText.setVisibility(View.INVISIBLE);
                            requestJustEnded = true;
                            undoDrawRequestedNum = 1;
                        } else if (drawRequestedNum == 0){
                            undoDrawRequestedNum = 0;
                        }

                        // insures only move following a double pawn move can complete en passant
                        if (pawnMovedDouble > 1)
                            pawnMovedDouble--;
                        else if (pawnMovedDouble == 1) {
                            pawnMovedDouble--;
                            ((Pawn)enPassantPawn).justMovedDouble = false;
                        }

                    } else {
                        drawRequested = false;
                        firstPieceSet = false;
                        secondPieceSet = false;

                        view.setEnabled(false);
                        out.println("Illegal move, try again");
                        out.println();

                        AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
                        builder.setTitle("Invalid move");
                        builder.setMessage("This move is invalid. Please try again.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                }
            }
        });

        for (int i=0;i<=7;i++){
            for (int j=0;j<=7;j++){
                int x = i+1;
                int y = j+1;
                String imageViewId = "image"+x+y;
                ImageView imageView = (ImageView)findViewById(getResources().getIdentifier(imageViewId,"id",getPackageName()));

                imageView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        // set chosen piece
                        String identifier = view.getResources().getResourceEntryName(view.getId());
                        identifier = identifier.substring(identifier.length()-2);
                        String numToLetter = letterConversion[Integer.parseInt(identifier.substring(1))-1];
                        identifier = numToLetter+identifier.substring(0,1);

                        out.println("Clicked on: " + identifier);



                        if (!firstPieceSet){
                            firstPiece = identifier;
                            firstPieceSet = true;
                        } else if (!secondPieceSet){
                            secondPiece = identifier;
                            secondPieceSet = true;
                            moveButton.setEnabled(true);
                        } else {
                            // both pieces are set
                        }

                    }
                });


            }
        }



        /*Scanner input = new Scanner(System.in);

        gameOver = true; // REMOVE



        while(!gameOver) {

            while (!validMove) {
                if (isWhiteTurn) {
                    if (whiteChecked) {
                        System.out.println("Check");
                        System.out.println();
                    }
                    System.out.print("White's move: ");
                }else {
                    if (blackChecked) {
                        System.out.println("Check");
                        System.out.println();
                    }
                    System.out.print("Black's move: ");
                }

                String instruction = input.nextLine();
                System.out.println();

                // resign
                if (instruction.equals("resign")) {
                    if (isWhiteTurn)
                        System.out.println("Black wins");
                    else
                        System.out.println("White wins");

                    gameOver = true;
                    break;
                }

                // draw
                if (instruction.length()>=6 && instruction.substring(6).equals("draw?")) {
                    drawRequested = true;
                } else if (instruction.equals("draw")) {
                    if (drawRequested) {
                        System.out.println("draw");
                        gameOver = true;
                        break;
                    } else {
                        System.out.println("Illegal move, try again");
                        System.out.println();
                        continue;
                    }
                } else {
                    if (drawRequested) {
                        drawRequested = false;
                    }
                }

                validMove = executeInstruction(instruction);

                if (validMove) {
                    if (isWhiteTurn)
                        whiteChecked = false;
                    else
                        blackChecked = false;

                    // if opponent's king is in check
                    String checkColor;
                    if (isWhiteTurn)
                        checkColor = "black";
                    else
                        checkColor = "white";

                    if (kingCheck(checkColor)) {
                        // checks for checkmate

                        if (!noLegalMoves(checkColor)) {
                            if (isWhiteTurn)
                                blackChecked = true;
                            else
                                whiteChecked = true;
                        } else {
                            // game is over
                            drawBoard();
                            gameOver = true;
                            System.out.println("Checkmate");
                            System.out.println();
                            if (isWhiteTurn) {
                                System.out.println("White wins");
                            } else {
                                System.out.println("Black wins");
                            }
                            break;
                        }
                    } else {
                        // no moves left for opponent but king isn't in check (stalemate)
                        if (noLegalMoves(checkColor)) {
                            drawBoard();
                            System.out.println("Stalemate");
                            System.out.println();
                            System.out.println("draw");
                            gameOver = true;
                            break;
                        }
                    }

                    drawBoard();
                    isWhiteTurn = !isWhiteTurn;

                } else {
                    drawRequested = false;
                    System.out.println("Illegal move, try again");
                    System.out.println();
                }
            }

            // insures only move following a double pawn move can complete en passant
            if (pawnMovedDouble > 1)
                pawnMovedDouble--;
            else if (pawnMovedDouble == 1) {
                pawnMovedDouble--;
                ((Pawn)enPassantPawn).justMovedDouble = false;
            }

        }

        input.close();*/

       // while (!tester);

    }

    public void endGame(String winner, boolean isCheckMate){
        // disable all listeners and display winner
        // string winner can be "black", "white", or "draw"
        // shows toast that shows name of saved game
        // takes user back to front page on home button

        //disable buttons
        moveButton.setVisibility(View.INVISIBLE);
        resignButton.setVisibility(View.INVISIBLE);
        drawButton.setVisibility(View.INVISIBLE);
        turnText.setVisibility(View.INVISIBLE);
        helpButton.setVisibility(View.INVISIBLE);
        undoButton.setVisibility(View.INVISIBLE);

        homeButton.setVisibility(View.VISIBLE);
        saveGameButton.setVisibility(View.VISIBLE);

        // display winner
        String upperCase = winner.substring(0,1).toUpperCase() + winner.substring(1);
        if (isCheckMate) {
            checkMateText.setVisibility(View.VISIBLE);
            checkMateText.setText("Checkmate: "+upperCase+" wins!");
        } else if (winner.equalsIgnoreCase("draw")){
            winnerText.setVisibility(View.VISIBLE);
            winnerText.setText(upperCase+"!");
        } else if (winner.equalsIgnoreCase("stalemate")){
            winnerText.setVisibility(View.VISIBLE);
            winnerText.setText("StaleMate: Draw");
        } else {
            winnerText.setVisibility(View.VISIBLE);
            winnerText.setText(upperCase+" wins!");
        }


        //disable chess board listeners
        for (int i=0;i<=7;i++) {
            for (int j = 0; j <= 7; j++) {
                int x = i + 1;
                int y = j + 1;
                String imageViewId = "image" + x + y;
                ImageView imageView = (ImageView) findViewById(getResources().getIdentifier(imageViewId, "id", getPackageName()));
                imageView.setOnClickListener(null);
            }
        }

        homeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // take user back to home
                finish();
                // show saved name in toast
            }
        });

        saveGameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // prompt user for save name
                final Dialog nameDialog = new Dialog(currContext);
                nameDialog.setContentView(R.layout.save_name_request);
                final EditText editText = (EditText)nameDialog.findViewById(R.id.editText);
                Button saveButton = (Button)nameDialog.findViewById(R.id.saveButton);

                saveButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        saveName = editText.getText().toString();
                        nameDialog.cancel();

                        try {

                            File directory = currContext.getFilesDir();
                            File file = new File(directory, "savedGames");

                            BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));

                            JSONObject gameSave = new JSONObject();
                            gameSave.put("Name",saveName);

                            JSONArray instructions = new JSONArray();
                            for (String instruction: savedInstructions){
                                instructions.put(instruction);
                            }
                            gameSave.put("Instructions",instructions);

                            writer.write(gameSave.toString());

                            writer.close();

                           /* FileOutputStream outputStream = openFileOutput("savedGames", Context.MODE_APPEND);

                            *//*File file = currContext.getFileStreamPath("savedGames");
                            if (!file.exists()){
                                System.out.println("AAAAAYYYYEEEEE");
                            } else {
                                 outputStream = openFileOutput("savedGames", Context.MODE_PRIVATE);
                            }*//*


                            outputStream.write(gameSave.toString().getBytes());
                            outputStream.write(System.getProperty("line.separator").getBytes());
                            outputStream.close();*/

                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("!!!!!!!!!!!!!!!!  2222");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("!!!!!!!!!!!!!!!! 55555");

                        }

                        // create json format save (like song library) grouped by save name containing savedInstructions array


                        // take user back to home
                        finish();


                    }
                });
                nameDialog.show();

            }
        });
    }

    // given a color, it does a move if possible
    public boolean computerMove(String color){
        String instruction;
        String first;
        String second;
        boolean isPromotion = false;

        for (int i=0;i<=7;i++) {
            for (int j=0;j<=7;j++) {
                Piece piece = board[i][j];

                if (piece != null && piece.getColor().equals(color)) {
                    first = letterConversion[piece.getX()]+(piece.getY()+1);

                    for (int m=0;m<=7;m++) {
                        for (int n=0;n<=7;n++) {
                            if (piece.move(m, n)) {
                                second = letterConversion[m]+(n+1);
                                instruction = first+ " " + second;
                                // promotion
                                if (piece instanceof Pawn){
                                    if (((Pawn) piece).canPromote) {
                                        ((Pawn)piece).Promote();
                                        isPromotion = true;
                                    }

                                    // en passant
                                    if (((Pawn) piece).justMovedDouble) {
                                        pawnMovedDouble = 2;
                                        if (enPassantPawn != null)
                                            ((Pawn)enPassantPawn).justMovedDouble = false;
                                        enPassantPawn = piece;

                                    }
                                }


                                if (isWhiteTurn)
                                    whiteChecked = false;
                                else
                                    blackChecked = false;

                                // if opponent's king is in check
                                String checkColor;
                                if (isWhiteTurn)
                                    checkColor = "black";
                                else
                                    checkColor = "white";

                                if (kingCheck(checkColor)) {
                                    // checks for checkmate

                                    if (!noLegalMoves(checkColor)) {
                                        if (isWhiteTurn)
                                            blackChecked = true;
                                        else
                                            whiteChecked = true;
                                    } else {
                                        // game is over
                                        drawActualBoard();;
                                        if (isWhiteTurn)
                                            endGame("White",true);
                                        else
                                            endGame("Black",true);
                                    }
                                } else {
                                    // no moves left for opponent but king isn't in check (stalemate)
                                    if (noLegalMoves(checkColor)) {
                                        drawActualBoard();
                                        out.println("Stalemate");
                                        out.println();
                                        out.println("draw");
                                        gameOver = true;
                                        endGame("stalemate",false);

                                    }
                                }

                                if (isPromotion)
                                    savedInstructions.add(instruction+" q");
                                else
                                    savedInstructions.add(instruction);

                                drawActualBoard();
                                firstPieceSet = false;
                                secondPieceSet = false;
                                isWhiteTurn = !isWhiteTurn;
                                moveButton.setEnabled(false);
                                if (isWhiteTurn){
                                    if (whiteChecked)
                                        turnText.setText("White's turn: CHECK!");
                                    else
                                        turnText.setText("White's turn");
                                } else{
                                    if (blackChecked){
                                        turnText.setText("Black's turn: CHECK!");
                                    } else
                                        turnText.setText("Black's turn");
                                }

                                if (drawRequestedNum > 1){
                                    drawText.setVisibility(View.VISIBLE);
                                    drawNotification.setVisibility(View.INVISIBLE);
                                    drawRequestedNum--;
                                } else if (drawRequestedNum == 1){
                                    drawRequestedNum--;
                                    drawText.setVisibility(View.INVISIBLE);
                                }

                                // insures only move following a double pawn move can complete en passant
                                if (pawnMovedDouble > 1)
                                    pawnMovedDouble--;
                                else if (pawnMovedDouble == 1) {
                                    pawnMovedDouble--;
                                    ((Pawn)enPassantPawn).justMovedDouble = false;
                                }
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * sets up the initial game board with all pieces in correct spot
     */
    public static void initializeBoard() {
        board = new Piece[8][8];

        // populate board with pieces

        //pawns
        for (int j=0;j<8;j++) {
            board[j][6] = new Pawn(j,6,"black");
            board[j][1] = new Pawn(j,1,"white");
        }

        //rooks
        board[0][7] = new Rook(0,7,"black");
        board[7][7] = new Rook(7,7,"black");

        board[0][0] = new Rook(0,0,"white");
        board[7][0] = new Rook(7,0,"white");

        //knights
        board[1][7] = new Knight(1,7,"black");
        board[6][7] = new Knight(6,7,"black");

        board[1][0] = new Knight(1,0,"white");
        board[6][0] = new Knight(6,0,"white");

        //bishops
        board[2][7] = new Bishop(2,7,"black");
        board[5][7] = new Bishop(5,7,"black");

        board[2][0] = new Bishop(2,0,"white");
        board[5][0] = new Bishop(5,0,"white");

        //queens
        board[3][7] = new Queen(3,7,"black");

        board[3][0] = new Queen(3,0,"white");

        //kings
        board[4][7] = new King(4,7,"black");

        board[4][0] = new King(4,0,"white");


    }

    /**
     * method that draws the current state of the board to the log
     */
    public static void drawBoard() {
        for (int i=7;i>=0;i--) {
            for (int j=0;j<8;j++) {
                if (board[j][i] == null) {
                    if ((j%2 != 0 && i%2 == 0) || (j%2 == 0 && i%2 != 0)) {
                        out.print("## ");
                    } else {
                        out.print("   ");
                    }
                } else {
                    out.print(board[j][i] + " ");
                }

            }
            out.println(i+1);
        }

        out.println(" a  b  c  d  e  f  g  h");
        out.println();
    }

    public void drawActualBoard(){
        // loop through board
        // for each entry
            // if null
                // set imageView for corresponding spot to transparent
            // else
                // set imageView to correct piece and color
        for (int i=0;i<=7;i++) {
            for (int j=0;j<8;j++) {
                int x = i+1;
                int y = j+1;
                String imageId = "image"+x+y;
                int id = getResources().getIdentifier(imageId, "id", getPackageName());
                ImageView imageView = (ImageView)findViewById(id);
                Context context = imageView.getContext();
              //  imageView.setImageResource(getResources().getIdentifier("blackrook","drawable",getPackageName()));


                if (board[j][i] == null) {
                    // set imageView to transparent
                   // imageView.setVisibility(View.INVISIBLE);
                    imageView.setImageDrawable(null);

                } else {
                    // set to correct piece image
                    Piece curr = board[j][i];

                    String pieceImage = curr.getColor()+curr.getClass().getSimpleName().toLowerCase();
                    int drawableId = context.getResources().getIdentifier(pieceImage, "drawable", context.getPackageName());

                    imageView.setImageResource(drawableId);
                    imageView.setVisibility(View.VISIBLE);

                }

            }
        }
    }

    /**
     * given a color, finds it's king, and sees if it is in check
     * @param color This is the color of the king being searched for and checked
     * @return boolean This returns whether or not the king of the color requested is in check
     */
    public static boolean kingCheck(String color) {

        // find king
        for (int i=0;i<=7;i++) {
            for (int j=0;j<=7;j++) {
                Piece temp = board[i][j];
                if (temp != null && temp instanceof King) {
                    if (temp.getColor().equals(color))
                        return ((King) temp).isCheck(temp.getX(), temp.getY());
                }

            }
        }

        return false;
    }

    /**
     *  checks if there are any legal moves a player can make given a color
     *  @param color This is the color of the player who's legal moves are searched
     *  @return boolean This returns whether or not the selected player has any legal moves left to make
     */
    public static boolean noLegalMoves(String color) {
        // loop through all places on the board
        // if piece of selected color is found
        // call piece.isValid with all possible places on the board
        // if isValid is true
        // make the move
        // call ischeck on king of that color
        // if no check
        // return false
        // undo move

        for (int i=0;i<=7;i++) {
            for (int j=0;j<=7;j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.getColor().equals(color)) {
                    for (int m=0;m<=7;m++) {
                        for (int n=0;n<=7;n++) {
                            if (piece.testMove(m, n)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
    /**
     * given user input, this method tries to execute the instruction and lets the caller know if it was executed successfully or not
     * @param instruction This is the user instruction that the method tries to execute
     * @return boolean This is where the method tells the caller if the instruction was executed successfully or not
     */
    public boolean executeInstruction(final String instruction) {
        String selectedPiece = instruction.substring(0, 2);
        String selectedSpot = instruction.substring(3, 5);

        int pieceX, pieceY, spotX, spotY;

        switch (selectedPiece.charAt(0)) {
            case 'a':
                pieceX = 0;
                break;
            case 'b':
                pieceX = 1;
                break;
            case 'c':
                pieceX = 2;
                break;
            case 'd':
                pieceX = 3;
                break;
            case 'e':
                pieceX = 4;
                break;
            case 'f':
                pieceX = 5;
                break;
            case 'g':
                pieceX = 6;
                break;
            case 'h':
                pieceX = 7;
                break;
            default:
                pieceX = -1;
        }

        pieceY = Integer.parseInt(selectedPiece.substring(1)) - 1;

        switch (selectedSpot.charAt(0)) {
            case 'a':
                spotX = 0;
                break;
            case 'b':
                spotX = 1;
                break;
            case 'c':
                spotX = 2;
                break;
            case 'd':
                spotX = 3;
                break;
            case 'e':
                spotX = 4;
                break;
            case 'f':
                spotX = 5;
                break;
            case 'g':
                spotX = 6;
                break;
            case 'h':
                spotX = 7;
                break;
            default:
                spotX = 10;
        }

        spotY = Integer.parseInt(selectedSpot.substring(1)) - 1;

        final Piece piece = board[pieceX][pieceY];

        // ensure user only moves their piece
        boolean allowedPiece = false;
        if (piece != null) {
            if (isWhiteTurn && piece.getColor().equals("white"))
                allowedPiece = true;
            else if (!isWhiteTurn && piece.getColor().equals("black"))
                allowedPiece = true;
        }


        if (allowedPiece) {
            //user requesting castling
            if (piece instanceof King) {
                if (spotY == piece.getY() && (spotX == piece.getX()-2 || spotX == piece.getX()+2)) {
                    if (((King)piece).castle(spotX,spotY)) {
                        savedInstructions.add(instruction + " c");
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            out.println("TEST1: board[0][1] = "+board[0][1]);

            // set up undo
            if (piece.testMove(spotX,spotY)) {
                out.println("TEST2: board[0][1] = "+board[0][1]);

                for (int i=0;i<=7;i++){
                    for (int j=0;j<=7;j++){
                        undoBoard[i][j] = board[i][j];
                    }
                }

                undoBlackChecked = blackChecked;
                undoWhiteChecked = whiteChecked;
                undoEnPassantPawn = enPassantPawn;
                undoIsWhiteTurn = isWhiteTurn;
                undoPawnMovedDouble = pawnMovedDouble;

                out.println("TEST4.5: undoboard[0][1] = "+undoBoard[0][1]);

            }

            if (piece.move(spotX, spotY)) {

                if (piece instanceof Pawn) {

                    // promotion
                    if (((Pawn) piece).canPromote) {
                        boolean isPromotion = false;
                        String promotionCode;

                        final Dialog dialog = new Dialog(this);
                        dialog.setContentView(R.layout.promotion_spinner);
                        Button promoteButton = (Button)dialog.findViewById(R.id.promoteButton);
                        Spinner spinner = (Spinner)dialog.findViewById(R.id.promoteSpinner);
                        final List<String> typesOfPieces = new ArrayList<String>();
                        typesOfPieces.add("Queen");
                        typesOfPieces.add("Rook");
                        typesOfPieces.add("Bishop");
                        typesOfPieces.add("Knight");

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,typesOfPieces);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        out.println("!!!! "+spinner);
                        spinner.setAdapter(adapter);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override

                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                // TODO Auto-generated method stub
                                promoteTo = typesOfPieces.get(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // TODO Auto-generated method stub

                            }
                        });

                        promoteButton.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view){
                                String promotionCode = "";

                                switch (promoteTo){
                                    case "Queen":
                                        promotionCode = " q";
                                        break;
                                    case "Knight":
                                        promotionCode = " n";
                                        break;
                                    case "Rook":
                                        promotionCode = " r";
                                        break;
                                    case "Bishop":
                                        promotionCode = " b";
                                }

                                String promoteInstruction = instruction+promotionCode;
                                out.println("!!!!!!!!! "+promoteInstruction);
                                ((Pawn)piece).Promote(promoteInstruction);
                                drawActualBoard();
                                dialog.cancel();

                                savedInstructions.add(promoteInstruction);
                            }
                        });

                        dialog.show();

                    } else {
                        // en passant
                        if (((Pawn) piece).justMovedDouble) {
                            pawnMovedDouble = 2;
                            if (enPassantPawn != null)
                                ((Pawn)enPassantPawn).justMovedDouble = false;
                            enPassantPawn = piece;

                        }
                        savedInstructions.add(instruction);

                    }

                } else {
                    savedInstructions.add(instruction);
                }


                return true;
            }
        }

        return false;
    }

}
