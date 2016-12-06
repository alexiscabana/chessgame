package com.equipe4.thechessgame;

import android.app.Application;

import com.equipe4.thechessgame.ChessboardUtils.Piece;
import com.equipe4.thechessgame.ChessboardUtils.PieceKey;
import com.equipe4.thechessgame.Configuration.ApparenceConfiguration;
import com.equipe4.thechessgame.GameManager.Player;
import com.equipe4.thechessgame.RequestsUtils.RequestSender;

/**
 * Class used to initialize the application's internal data and model.
 *
 * Created by Alexis on 2016-10-26.
 */
public class Platform extends Application {

    /**
     * First thing executed in the application's context
     */
    @Override
    public void onCreate() {
        super.onCreate();

        //Initialize the application's request queue
        RequestSender.createInstance(getApplicationContext());

        //Initialize the default Bitmap configurations for pieces
        ApparenceConfiguration ac = ApparenceConfiguration.getInstance();
        ac.setResourcesProvider(getResources());

        Piece.PieceType[] allPieceTypes = {
                Piece.PieceType.king,
                Piece.PieceType.queen,
                Piece.PieceType.rook,
                Piece.PieceType.bishop,
                Piece.PieceType.knight,
                Piece.PieceType.pawn,
        };

        Player[] playerTypes = {
                Player.getInstancePlayerWhite(),
                Player.getInstancePlayerBlack()
        };

        /* all the resource IDs for the default piece set */
        Integer[] idsDefaultSet = {
                R.drawable.king_blanc_default,
                R.drawable.queen_blanc_default,
                R.drawable.rook_blanc_default,
                R.drawable.bishop_blanc_default,
                R.drawable.knight_blanc_default,
                R.drawable.pawn_blanc_default,
                R.drawable.king_noir_default,
                R.drawable.queen_noir_default,
                R.drawable.rook_noir_default,
                R.drawable.bishop_noir_default,
                R.drawable.knight_noir_default,
                R.drawable.pawn_noir_default,
        };

        /* all the resource IDs for the piece set #1*/
        Integer[] idsSet1 = {
                R.drawable.king_blanc_set1,
                R.drawable.queen_blanc_set1,
                R.drawable.rook_blanc_set1,
                R.drawable.bishop_blanc_set1,
                R.drawable.knight_blanc_set1,
                R.drawable.pawn_blanc_set1,
                R.drawable.king_noir_set1,
                R.drawable.queen_noir_set1,
                R.drawable.rook_noir_set1,
                R.drawable.bishop_noir_set1,
                R.drawable.knight_noir_set1,
                R.drawable.pawn_noir_set1,
        };

        Integer[] idsSet2 = {
                R.drawable.king_white_set2,
                R.drawable.queen_white_set2,
                R.drawable.rook_white_set2,
                R.drawable.bishop_white_set2,
                R.drawable.knight_white_set2,
                R.drawable.pawn_white_set2,
                R.drawable.king_black_set2,
                R.drawable.queen_black_set2,
                R.drawable.rook_black_set2,
                R.drawable.bishop_black_set2,
                R.drawable.knight_black_set2,
                R.drawable.pawn_black_set2,
        };

        Integer[] idsSet3 = {
                R.drawable.king_white_set3,
                R.drawable.queen_white_set3,
                R.drawable.rook_white_set3,
                R.drawable.bishop_white_set3,
                R.drawable.knight_white_set3,
                R.drawable.pawn_white_set3,
                R.drawable.king_black_set3,
                R.drawable.queen_black_set3,
                R.drawable.rook_black_set3,
                R.drawable.bishop_black_set3,
                R.drawable.knight_black_set3,
                R.drawable.pawn_black_set3,
        };

        Integer[] idsSet4 = {
                R.drawable.king_blanc_set4,
                R.drawable.queen_blanc_set4,
                R.drawable.rook_blanc_set4,
                R.drawable.bishop_blanc_set4,
                R.drawable.knight_blanc_set4,
                R.drawable.pawn_blanc_set4,
                R.drawable.king_noir_set4,
                R.drawable.queen_noir_set4,
                R.drawable.rook_noir_set4,
                R.drawable.bishop_noir_set4,
                R.drawable.knight_noir_set4,
                R.drawable.pawn_noir_set4,
        };
        //identify each piece set with its corresponding resources
        int n = 0;
        for (Player playerType : playerTypes)
            for (Piece.PieceType allPieceType : allPieceTypes)
                ac.addBitmapParam(new PieceKey(new Piece(allPieceType, playerType)),
                        ApparenceConfiguration.PieceTag.default_set, idsDefaultSet[n++]);

        n = 0;
        for (Player playerType : playerTypes)
            for (Piece.PieceType allPieceType : allPieceTypes)
                ac.addBitmapParam(new PieceKey(new Piece(allPieceType, playerType)),
                        ApparenceConfiguration.PieceTag.set_1, idsSet1[n++]);

        n = 0;
        for (Player playerType : playerTypes)
            for (Piece.PieceType allPieceType : allPieceTypes)
                ac.addBitmapParam(new PieceKey(new Piece(allPieceType, playerType)),
                        ApparenceConfiguration.PieceTag.set_2, idsSet2[n++]);

        n = 0;
        for (Player playerType : playerTypes)
            for (Piece.PieceType allPieceType : allPieceTypes)
                ac.addBitmapParam(new PieceKey(new Piece(allPieceType, playerType)),
                        ApparenceConfiguration.PieceTag.set_3, idsSet3[n++]);

        n = 0;
        for (Player playerType : playerTypes)
            for (Piece.PieceType allPieceType : allPieceTypes)
                ac.addBitmapParam(new PieceKey(new Piece(allPieceType, playerType)),
                        ApparenceConfiguration.PieceTag.set_4, idsSet4[n++]);


        //set the default to be the current set to use
        ac.setGroupAsCurrentParams(ApparenceConfiguration.PieceTag.default_set);


        //-----------------------------BOARD--------------------------------------------------------
        //Decode all the chessboards
        ac.addBitmapParam(ApparenceConfiguration.Params.chessboard_bitmap_param,
                ApparenceConfiguration.BoardTag.board_default, R.drawable.chessboard_default);
        ac.addBitmapParam(ApparenceConfiguration.Params.chessboard_bitmap_param,
                ApparenceConfiguration.BoardTag.board_1, R.drawable.chessboard_1);
        ac.addBitmapParam(ApparenceConfiguration.Params.chessboard_bitmap_param,
                ApparenceConfiguration.BoardTag.board_2, R.drawable.chessboard_2);
        ac.addBitmapParam(ApparenceConfiguration.Params.chessboard_bitmap_param,
                ApparenceConfiguration.BoardTag.board_3, R.drawable.chessboard_3);

        //set the default to be the current set to use
        ac.setGroupAsCurrentParams(ApparenceConfiguration.BoardTag.board_default);
    }
}