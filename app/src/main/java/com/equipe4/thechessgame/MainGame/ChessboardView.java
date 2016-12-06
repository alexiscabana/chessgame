package com.equipe4.thechessgame.MainGame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.equipe4.thechessgame.ChessboardUtils.ChessBoardObject;
import com.equipe4.thechessgame.ChessboardUtils.ChessboardPosition;
import com.equipe4.thechessgame.Configuration.ApparenceConfiguration;
import com.equipe4.thechessgame.Configuration.BitmapContentProvider;
import com.equipe4.thechessgame.ChessboardUtils.Piece;

/**
 * View used by an activity. It is used to draw a chessboard on screen as well as pieces.
 *
 * Created by Alexis on 2016-10-17.
 */

public class ChessboardView extends ImageView {

    public static final int NB_CASE_CHESSBOARD = 8;

    //------------------------PRIVATE FIELDS--------------------------------------------------------
    private boolean isLocked = false;
    private BitmapContentProvider contentProvider = null;
    private ChessBoardObject chessboard = null;
    private ChessboardPosition itemSelected = null;
    private SelectionState selectState = SelectionState.NO_SELECT;
    private Paint itemSelectedPaint = null;
    private Paint itemSelectedContourPaint = null;
    private Click currentClick, lastClick;
    private PieceMoveListener moveListener;
    private Rect chessboardRect = null;
    //private Paint possibleCasePaint = null;

    //------------------------CONSTRUCTORS----------------------------------------------------------
    public ChessboardView(Context context){
        super(context);
        init();
    }
    public ChessboardView(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }
    public ChessboardView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init();
    }

    //------------------------PUBLIC API------------------------------------------------------------
    @Override
    public void onDraw(Canvas canvas){
        //draw the board before...
        Bitmap board = contentProvider.getBitmapFor(ApparenceConfiguration.Params.chessboard_bitmap_param);
        canvas.drawBitmap(board, null, getDrawRect(), null);


        //...then draw the square of current selection..
        if(itemSelected != null) {
            canvas.drawRect(
                    getCaseRect(itemSelected.getColumn(), itemSelected.getRow()),
                    itemSelectedPaint);
            canvas.drawRect(
                    getCaseRect(itemSelected.getColumn(), itemSelected.getRow()),
                    itemSelectedContourPaint);
        }

        if(chessboard == null)
            return;

        //... then draw the pieces after
        for(int i = 1; i<= NB_CASE_CHESSBOARD; i++)
            for(char c = 'a'; c <= 'h'; c++){
                Piece p = chessboard.getPieceAt(c, i);
                if (p != null) {
                    //get the associated bitmap
                    Bitmap b = contentProvider.getBitmapFor(p);

                    if(b != null)
                        canvas.drawBitmap(b, null, getCaseRect(c,i), null);
                }
            }
    }

    /**
     * Sets the board object this ChessboardView should draw from.
     *
     * @param board the board to draw
     */
    public void setChessboard(ChessBoardObject board){
        this.chessboard = board;
        redraw();
    }
    /**
     * A call to this method invalidates the current view, thus forcing a redrawing.
     */
    public void redraw() {
        invalidate();
    }

    public void setCaseSelectedColor(int color){
        itemSelectedPaint.setColor(color);
        itemSelectedContourPaint.setColor(color);
    }
    public void setCaseSelectedAlpha(int alpha){
        itemSelectedPaint.setAlpha(alpha);
    }
    //public void setPossibleMoveCaseColor(int color){ itemSelectedPaint.setColor(color); }
    //public void setPossibleMoveCaseAlpha(int alpha){ itemSelectedPaint.setAlpha(alpha); }
    public void setBitmapContentProvider(@NonNull BitmapContentProvider newProvider){
        this.contentProvider = newProvider;
    }
    public void setOnPieceMoveListener(PieceMoveListener list) { this.moveListener = list; }
    public void lock(){ this.isLocked = true; }
    public void unlock(){ this.isLocked = false; }
    public boolean isLocked() { return isLocked; }

    //------------------------PRIVATE METHODS-------------------------------------------------------
    private void init() {
        setWillNotDraw(false);
        itemSelectedPaint = new Paint();
        itemSelectedContourPaint = new Paint();
        itemSelectedContourPaint.setStyle(Paint.Style.STROKE);
        itemSelectedContourPaint.setStrokeWidth(10);
        //possibleCasePaint = new Paint();
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isLocked())
                    return false;
                if(!getDrawRect().contains((int)event.getX(),(int)event.getY()))
                    return true;
                if(event.getActionMasked() == MotionEvent.ACTION_DOWN)
                {
                    currentClick = new Click(new TouchPosition((int)event.getX(),(int)event.getY()));
                }
                else if (event.getActionMasked() == MotionEvent.ACTION_UP)
                {
                    if(currentClick == null) // if the beginning of the touch was out of bounds
                        return true;
                    currentClick.setEnding(new TouchPosition((int)event.getX(),(int)event.getY()));
                    newClick(currentClick);
                }

                return true;
            }
        });
        setOnPieceMoveListener(new PieceMoveListener() {
            @Override
            public void onPieceMove(ChessboardPosition source, ChessboardPosition dest) {}
        }); // listener vide
        int maxSquareSize = Math.min(getWidth(),getHeight());
        setMaxWidth(maxSquareSize);
        setMaxHeight(maxSquareSize);
        invalidate();
    }
    private Rect getCaseRect(char x, int y) {
        int heightCase = (int)(getDrawRect().height() / (float)NB_CASE_CHESSBOARD);
        int widthCase  = (int)(getDrawRect().width()  / (float)NB_CASE_CHESSBOARD);
        int left = (x - 'a') * widthCase + getDrawRect().left;
        int top  = getDrawRect().height() - y * heightCase;
        int right = (x - 'a' + 1) * widthCase + getDrawRect().left;
        int bottom = getDrawRect().width() - (y - 1) * heightCase;

        return new Rect(left,top,right,bottom);
    }
    private void newClick(Click click) {
        // if the span of the click is more than the threshold, it's considered a new selection
        if(click.distanceOriginToEnding() > 50 && selectState == SelectionState.NO_SELECT){
            newMove(click.getOrigin(), click.getEnding());
            return;
        }
        //if it's an instantaneous click
        if(selectState == SelectionState.NO_SELECT){
            lastClick = currentClick;
            selectItem(touchPosToCase(click.getEnding()));
            selectState = SelectionState.ITEM_SELECTED; //if there was no item selected, now there's one
        } else if(selectState == SelectionState.ITEM_SELECTED){
            newMove(lastClick.getOrigin(), click.getEnding()); // if there was a selected item, notify there's a new selection
            selectItem(null);
            selectState = SelectionState.NO_SELECT;
        }

    }
    private void selectItem(ChessboardPosition Case) {
        setCaseSelected(Case);
    }
    private ChessboardPosition touchPosToCase(TouchPosition pos){
        //position is relative to the current view, not the activity
        float largeurColonne = (float)getDrawRect().width() / ChessboardView.NB_CASE_CHESSBOARD;
        float largeurRangee = (float)getDrawRect().height() / ChessboardView.NB_CASE_CHESSBOARD;

        Character colonne = (char)((int)Math.floor((pos.getX() - getDrawRect().left)/ largeurColonne) + 'a');
        Integer rangee  = ChessboardView.NB_CASE_CHESSBOARD - (int)Math.floor((pos.getY() - getDrawRect().top)/ largeurRangee);
        return new ChessboardPosition(colonne, rangee);
    }
    private void setCaseSelected(@NonNull ChessboardPosition cbp){
        this.itemSelected = cbp;
        redraw();
    }
    private void newMove(TouchPosition origin, TouchPosition ending){
        moveListener.onPieceMove(touchPosToCase(origin), touchPosToCase(ending));
    }
    private Rect getDrawRect() {
        int maxLengthSide = Math.min(getWidth(), getHeight()) * 4 / 5;
        int offsetX = (getWidth() - maxLengthSide) / 2;
        return new Rect(offsetX,0, maxLengthSide + offsetX, maxLengthSide);
    }
    public interface PieceMoveListener{
        void onPieceMove(ChessboardPosition source, ChessboardPosition dest);
    }
    private enum SelectionState{
        NO_SELECT,
        ITEM_SELECTED
    }
 }
