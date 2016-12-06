package com.equipe4.thechessgame.ChessboardUtils;


import com.equipe4.thechessgame.GameManager.Player;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Alexis on 2016-10-11.
 *
 * Class used to represent a checkboard.
 */
public class ChessBoardObject {

    //default size
    private static final Integer CHECKBOARD_SIZE = 8;

    private Piece[][] board = new Piece[CHECKBOARD_SIZE][CHECKBOARD_SIZE];

    public ChessBoardObject(){
        clearBoard();
        fillBoard();
    }

    //Public API -----------------------------------------------------------------
    /**
     * @param p the piece to set in place
     * @param pos the position of the piece
     *
     * @return if the value was correctly set
     */
    public boolean setPiece(Piece p, ChessboardPosition pos){
        if(!isValidCase(pos))
            return false;

        setPieceRaw(p, pos.getColumn(), pos.getRow());

        return true;
    }

    /**
     * @param x the character (x axis) of the case
     * @param y the y axis value of the case
     *
     * @return the piece at the case specified, null if there's no piece or the location was invalid
     */
    public Piece getPieceAt(char x, int y){
        if(!isValidCase(x,y))
            return null;
        return board[y - 1][x - 'a'];
    }

    public Piece getPieceAt(ChessboardPosition p){
        return getPieceAt(p.getColumn(),p.getRow());
    }
    /**
     * Method thats moves a piece from a source case to a destination case.
     * If there's already a piece in the destination case, it will be overridden by the source piece
     * If there's no piece in the source case, the move is considered unsuccessful. Thus, it will not
     * override the destination case.
     *
     * @param source the value of the source case
     * @param dest the value of the destination case;
     *
     * @return if the move was successful
     */
    public boolean movePiece(final ChessboardPosition source, final ChessboardPosition dest){

        Piece sourcep = getPieceAt(source);
        if(sourcep == null) //source case invalid or no piece in it
            return false;

        if(source.equals(dest))//source == dest
            return false;

        if(!setPiece(sourcep, dest)) //dest case invalid
            return false;

        //dest in now set
        setPiece(null, source); // nothing left in the source case

        return true;
    }
    /**
     * @return the current state of this Checkboard for an ulterior restore
     */
    public ChessboardObjectMemento saveToMemento(){
        Piece[][] currState = new Piece[CHECKBOARD_SIZE][CHECKBOARD_SIZE];

        //deep copy de l'état du checkboard
        for(int i = 0; i < CHECKBOARD_SIZE; i++)
            for(int j = 0; j < CHECKBOARD_SIZE; j++) {
                currState[i][j] = board[i][j] == null ? null : board[i][j].clonePiece();
            }

        return new ChessboardObjectMemento(currState);
    }
    /**
     * @param oldState the state of an old Checkboard to restore from
     */
    public void restoreFromMemento(ChessboardObjectMemento oldState){
        board = oldState.getSavedState();
    }
    /**
     * @return a non-modifiable version of the current board. If the Piece is null, then there is no
     * piece at this position.
     */
    public final Piece[][] getBoard(){
        return board;
    }
    /**
     * @return a JSON object representing the current board.
     */
    public final JSONObject toJSONObject(){
        JSONObject json = new JSONObject();

        int WHITE = Player.getInstancePlayerWhite().getNumeroJoueur();
        int BLACK = Player.getInstancePlayerBlack().getNumeroJoueur();
        try {
            json.put("king1",   getPositionsOf(Piece.PieceType.king,    '\0',   WHITE).get(0));
            json.put("queen1",  getPositionsOf(Piece.PieceType.queen,   '\0',   WHITE).get(0));
            json.put("bishop1A", getPositionsOf(Piece.PieceType.bishop, 'A',    WHITE).get(0));
            json.put("bishop1B", getPositionsOf(Piece.PieceType.bishop, 'B',    WHITE).get(0));
            json.put("rook1A",  getPositionsOf(Piece.PieceType.rook,    'A',    WHITE).get(0));
            json.put("rook1B",  getPositionsOf(Piece.PieceType.rook,    'B',    WHITE).get(0));
            json.put("knight1A", getPositionsOf(Piece.PieceType.knight, 'A',    WHITE).get(0));
            json.put("knight1B", getPositionsOf(Piece.PieceType.knight, 'B',    WHITE).get(0));

            List<String> pawnArray1 = getPositionsOf(Piece.PieceType.pawn, '\0', WHITE);
            while(pawnArray1.size() < CHECKBOARD_SIZE) pawnArray1.add("X");

            JSONArray pawns1 = new JSONArray(pawnArray1.toArray());
            json.put("pawn1", pawns1);

            json.put("king2",   getPositionsOf(Piece.PieceType.king,    '\0',   BLACK).get(0));
            json.put("queen2",  getPositionsOf(Piece.PieceType.queen,   '\0',   BLACK).get(0));
            json.put("bishop2A", getPositionsOf(Piece.PieceType.bishop, 'A',    BLACK).get(0));
            json.put("bishop2B", getPositionsOf(Piece.PieceType.bishop, 'B',    BLACK).get(0));
            json.put("rook2A",  getPositionsOf(Piece.PieceType.rook,    'A',    BLACK).get(0));
            json.put("rook2B",  getPositionsOf(Piece.PieceType.rook,    'B',    BLACK).get(0));
            json.put("knight2A", getPositionsOf(Piece.PieceType.knight, 'A',    BLACK).get(0));
            json.put("knight2B", getPositionsOf(Piece.PieceType.knight, 'B',    BLACK).get(0));

            List<String> pawnArray2 = getPositionsOf(Piece.PieceType.pawn, '\0', BLACK);
            while(pawnArray2.size() < CHECKBOARD_SIZE) pawnArray2.add("X");

            JSONArray pawns2 = new JSONArray(pawnArray2.toArray());
            json.put("pawn2", pawns2);
        } catch (JSONException e){

        }
        return json;
    }

    public boolean setFromJSON(JSONObject json)  {
        clearBoard();

        //set all the pieces from their keys
        Iterator<String> it = json.keys();
        while(it.hasNext()) {
            String key = it.next();
            String[] tokens = key.split("((?<=[12])|(?=[12]))"); //key is in form "piece1" or "piece2"
            if(tokens.length != 2)
                continue; // not a recognized piece

            try {
                JSONArray array = json.getJSONArray(key);
                for(int i = 0; i < array.length(); i++)
                    setPiece(
                            new Piece(Piece.PieceType.valueOf(
                                    tokens[0]),
                                    Player.getInstanceFromNumber(Integer.valueOf(tokens[1]))),
                            new ChessboardPosition(array.getString(i))
                    );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void reset(){
        clearBoard();
        fillBoard();
    }
    //Private Methods ------------------------------------------------------------
    /**
     * Gets the position of the the specified piece.
     *
     * @param type the type of the piece
     * @param id the id of the piece ('A', 'B' or '\0')
     * @param noJoueur the number of the player
     * @return a list of the cases that contains the description of the piece in argument
     */
    private List<String> getPositionsOf(Piece.PieceType type, Character id, int noJoueur){
        List<String> positions = new ArrayList<>();

        for(Character c = 'a'; c <= 'h'; c++)
            for(Integer i = 1; i <= CHECKBOARD_SIZE; i++) {
                Piece p = getPieceAt(c,i);
                if(p == null) continue;

                if(p.getType() == type  &&
                        p.getJoueurProprietaire().getNumeroJoueur() == noJoueur)
                    positions.add(c.toString() + i.toString());
            }
        if(positions.isEmpty()) positions.add("X");

        return positions;
    }
    private void fillBoard() {
        //Pieces du joueur 1
        Player BLANC = Player.getInstancePlayerWhite();
        Player NOIR = Player.getInstancePlayerBlack();
        setPiece(new Piece(Piece.PieceType.rook,  BLANC), 'a', 1);
        setPiece(new Piece(Piece.PieceType.knight,BLANC), 'b', 1);
        setPiece(new Piece(Piece.PieceType.bishop,BLANC), 'c', 1);
        setPiece(new Piece(Piece.PieceType.queen, BLANC), 'd', 1);
        setPiece(new Piece(Piece.PieceType.king,  BLANC), 'e', 1);
        setPiece(new Piece(Piece.PieceType.bishop,BLANC), 'f', 1);
        setPiece(new Piece(Piece.PieceType.knight,BLANC), 'g', 1);
        setPiece(new Piece(Piece.PieceType.rook,  BLANC), 'h', 1);
        //Pions du joueur 1
        setPiece(new Piece(Piece.PieceType.pawn, BLANC), 'a', 2);
        setPiece(new Piece(Piece.PieceType.pawn, BLANC), 'b', 2);
        setPiece(new Piece(Piece.PieceType.pawn, BLANC), 'c', 2);
        setPiece(new Piece(Piece.PieceType.pawn, BLANC), 'd', 2);
        setPiece(new Piece(Piece.PieceType.pawn, BLANC), 'e', 2);
        setPiece(new Piece(Piece.PieceType.pawn, BLANC), 'f', 2);
        setPiece(new Piece(Piece.PieceType.pawn, BLANC), 'g', 2);
        setPiece(new Piece(Piece.PieceType.pawn, BLANC), 'h', 2);

        //--------------------------------------------------------------
        //Pieces du joueur 2
        setPiece(new Piece(Piece.PieceType.rook,  NOIR), 'a', 8);
        setPiece(new Piece(Piece.PieceType.knight,NOIR), 'b', 8);
        setPiece(new Piece(Piece.PieceType.bishop,NOIR), 'c', 8);
        setPiece(new Piece(Piece.PieceType.queen, NOIR), 'd', 8);
        setPiece(new Piece(Piece.PieceType.king,  NOIR), 'e', 8);
        setPiece(new Piece(Piece.PieceType.bishop,NOIR), 'f', 8);
        setPiece(new Piece(Piece.PieceType.knight,NOIR), 'g', 8);
        setPiece(new Piece(Piece.PieceType.rook,  NOIR), 'h', 8);

        //Pions du joueur 2
        setPiece(new Piece(Piece.PieceType.pawn,NOIR), 'a', 7);
        setPiece(new Piece(Piece.PieceType.pawn,NOIR), 'b', 7);
        setPiece(new Piece(Piece.PieceType.pawn,NOIR), 'c', 7);
        setPiece(new Piece(Piece.PieceType.pawn,NOIR), 'd', 7);
        setPiece(new Piece(Piece.PieceType.pawn,NOIR), 'e', 7);
        setPiece(new Piece(Piece.PieceType.pawn,NOIR), 'f', 7);
        setPiece(new Piece(Piece.PieceType.pawn,NOIR), 'g', 7);
        setPiece(new Piece(Piece.PieceType.pawn,NOIR), 'h', 7);

    }
    private void clearBoard() {
        board = new Piece[CHECKBOARD_SIZE][CHECKBOARD_SIZE];
    }
    private void setPieceRaw(Piece p, char x, int y){
        board[y - 1][x - 'a'] = p;
        //board[x - 'a'][y - 1] = p;
    }
    private static boolean isValidCase(char x, int y){
        return (x >= 'a' && x <= 'h') && (y <= CHECKBOARD_SIZE && y >= 1) ;
    }
    private static boolean isValidCase(ChessboardPosition position){
        return isValidCase(position.getColumn(), position.getRow()) ;
    }
    private boolean setPiece(Piece p, char c, int i){
        return setPiece(p, new ChessboardPosition(c,i));
    }
}
