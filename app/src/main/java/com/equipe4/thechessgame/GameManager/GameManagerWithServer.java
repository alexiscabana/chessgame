package com.equipe4.thechessgame.GameManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import com.equipe4.thechessgame.ChessboardUtils.ChessboardPosition;
import com.equipe4.thechessgame.ChessboardUtils.Piece;
import com.equipe4.thechessgame.Configuration.GameConfiguration;
import com.equipe4.thechessgame.ChessboardUtils.ChessBoardObject;
import com.equipe4.thechessgame.GameManager.GameEvents.ChessboardChangedEvent;
import com.equipe4.thechessgame.GameManager.GameEvents.GameEndedEvent;
import com.equipe4.thechessgame.GameManager.GameEvents.IGameEvent;
import com.equipe4.thechessgame.GameManager.GameEvents.GameManagerEventListener;
import com.equipe4.thechessgame.GameManager.GameEvents.GameStartedEvent;
import com.equipe4.thechessgame.GameManager.GameEvents.GameStatusChangedEvent;
import com.equipe4.thechessgame.GameManager.GameEvents.PlayerChangedEvent;
import com.equipe4.thechessgame.GameManager.GameEvents.PawnPromotionEvent;
import com.equipe4.thechessgame.RequestsUtils.Encoder;
import com.equipe4.thechessgame.RequestsUtils.RequestSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexis on 2016-10-11.
 *
 * Model class that will be drawn on screen, part of the MVC. Used as a container for the current
 * game's data.The class also auto-updates itself via the requests. Ensures the state of the game
 * is coherent with the server. It is from this class that the UI should take its model.
 */
abstract class GameManagerWithServer implements IGameManager {

    //-------------------------PRIVATE FIELDS-------------------------------------------------------
    protected final ChessBoardObject board = new ChessBoardObject();
    protected volatile List<GameManagerEventListener> listeners = new ArrayList<GameManagerEventListener>();
    protected volatile Player playerCurrentlyPlaying = Player.getInstancePlayerWhite();
    protected volatile boolean isGameInProgress = false;
    protected volatile Integer totalMoveNo = 0;
    protected volatile String lastMoveCase = "";
    protected volatile GameStatusType gameStatus = GameStatusType.waiting_for_gamestart;
    protected volatile ChessboardPosition posPieceToPromote = null;

    GameManagerWithServer() {
    }

    //-------------------------PUBLIC API-----------------------------------------------------------
    /**
     * Method that sets the new listener for this class
     *
     * @param newlist the new listener to notify for changes
     */
    public void addGameStateListener(GameManagerEventListener newlist){
        listeners.add(newlist);
    }

    @Override
    public void deleteGameStateListener(GameManagerEventListener list) {
        listeners.remove(list);
    }
    /**
     * Method that starts the game by sending a com.equipe4.androidappinf3995_04.request to the server. It will trigger a lot of
     * events, like GameStatusChanged and JoueurChanged
     */
    public void startGame(final GameManagerResponseListener list){
        if(isGameInProgress())
            return;
        //ajouter requete pour commencer la partie
        String URI = GameConfiguration.getInstance().getServerURL()+ "/game_start";
        StringRequest req = new StringRequest(Request.Method.POST, URI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //on peut commencer la partie
                updateGameStatus(new GameManagerResponseListener() {}); //empty listener
                executeStartGame();
                list.onRequestSuccess();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                list.onRequestError(error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.putAll(super.getHeaders());

                String key = "Authorization";
                String auth = "Basic " + Encoder.getCodeBase64(getRequestPassword());

                headers.put(key, auth);
                return headers;
            }
        };
        RequestSender.getInstance().addRequest(req);
    }

    /**
     * Method that ends the current game if there's one currently playing
     * @param reason the explicit reason of the ending
     */
    public void endGame(final GameEndType reason, final GameManagerResponseListener list)  {
        if(!isGameInProgress())
            return;

        String URI = GameConfiguration.getInstance().getServerURL() + "/game_end";
        StringRequest req = new StringRequest(Request.Method.POST, URI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //on peut arreter la partie
                executeGameEnd(reason);
                list.onRequestSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                list.onRequestError(error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.putAll(super.getHeaders());

                String key = "Authorization";
                String auth = "Basic " + Encoder.getCodeBase64(getRequestPassword());

                headers.put(key, auth);
                return headers;
            }
        };
        RequestSender.getInstance().addRequest(req);
    }

    /**
     * Method that update the Gamestate's summary of the game. It should be called regularly to ensure
     * consistency with the server's com.equipe4.androidappinf3995_04.gamestate.
     */
    public void updateGameStatus(final GameManagerResponseListener list) {
        String URI = GameConfiguration.getInstance().getServerURL() + "/status/summary";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //update les informations du com.equipe4.androidappinf3995_04.gamestate avec ce qui est dans le JSON response
                Integer moveNo = null;
                String lastMove = null, turn = null, state = null, isWaitingForGameStart = null;

                try {
                    turn = (String) response.get("turn");
                    moveNo = response.getInt("moveNo");
                    lastMove = (String) response.get("lastMove");
                    state = (String) response.get("state");
                } catch (JSONException e) {
                    //error handling
                }

                if (turn != null)
                    setPlayerCurrentlyPlaying(Player.getInstanceFromNumber(Integer.valueOf(turn)));
                if (moveNo != null)
                    setTotalMoveNo(moveNo);
                if (lastMove != null)
                    setLastMoveCase(lastMove);
                if (state != null)
                    setGameStatus(GameStatusType.valueOf(state));

                list.onRequestSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                list.onRequestError(error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.putAll(super.getHeaders());

                String key = "Authorization";
                String auth = "Basic " + Encoder.getCodeBase64(getRequestPassword());

                headers.put(key, auth);
                return headers;
            }
        };
        RequestSender.getInstance().addRequest(req);
    }
    /**
     *
     * Method that updates the GameState's time_config_screen_layout variable left about a certain Joueur. It should be
     * called regularly to ensure consistency with the server's time_config_screen_layout.
     *
     * @param j The Joueur to update the time_config_screen_layout from.
     */
    public void updateRemainingTime(final Player j, final GameManagerResponseListener list){
        //Construction de la requete pour update le temps du joueur
        String URI = GameConfiguration.getInstance().getServerURL() + "/time/" + j.getNumeroJoueur().toString();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Integer time, overtime;
                try{
                    time = response.getInt("time");
                    overtime = response.getInt("overtime");
                } catch (JSONException e){
                    e.printStackTrace();
                    return;
                }
                j.getPlayerTime().setTimeRemainingSeconds(time);
                j.getPlayerTime().setOvertimeRemainingSeconds(overtime);
                list.onRequestSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                list.onRequestError(error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.putAll(super.getHeaders());

                String key = "Authorization";
                String auth = "Basic " + Encoder.getCodeBase64(getRequestPassword());

                headers.put(key, auth);
                return headers;
            }
        };
        RequestSender.getInstance().addRequest(req);
    }
    /**
     * Method that determines the next player to play.
     */
    public void determineNextPlayer(){
        if(!isGameInProgress())
            return; //cannot switch if there's no game currently being played

        setPlayerCurrentlyPlaying(
                playerCurrentlyPlaying == null ||
                playerCurrentlyPlaying.getType() == Player.PlayerType.BLACK ?
                        Player.getInstancePlayerWhite() : Player.getInstancePlayerBlack()
        );
    }
    /**
     * Method that sets the board one-way : a GET method sets the board to what the server has, or
     * a POST method sets what the server has to what there is in the tablet.
     * @param post true if it's a POST com.equipe4.androidappinf3995_04.request, false if it's a GET
     */
    public void updateStatusBoard(final boolean post, final GameManagerResponseListener list){

        String URI = GameConfiguration.getInstance().getServerURL() + "/status/board";
        JSONObject boardToPOST = null;
        if(post) {
            boardToPOST = board.toJSONObject();
        }

        JsonObjectRequest req = new JsonObjectRequest(post ? Request.Method.POST : Request.Method.GET,
                URI, boardToPOST, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if(!post)
                    board.setFromJSON(response);
                Integer moveNo = null, turn = null;
                try {
                    turn = response.getInt("turn");
                    moveNo = response.getInt("moveNo");
                } catch (JSONException e){
                    e.printStackTrace();
                }
                if(turn != null ) setPlayerCurrentlyPlaying(Player.getInstanceFromNumber(turn));
                if(moveNo != null ) setTotalMoveNo(moveNo);
                list.onRequestSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                list.onRequestError(error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.putAll(super.getHeaders());

                String key = "Authorization";
                String auth = "Basic " + Encoder.getCodeBase64(getRequestPassword());

                headers.put(key, auth);
                return headers;
            }
        };
        RequestSender.getInstance().addRequest(req);
    }

    public void promotePiece(final Piece.PieceType type, Player p, final GameManagerResponseListener list) {
        String URI = GameConfiguration.getInstance().getServerURL() + "/promote/" +
                p.getNumeroJoueur().toString() + "/" + type.toString();

        StringRequest req = new StringRequest(Request.Method.POST, URI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(posPieceToPromote != null){
                    board.setPiece(new Piece(type, board.getPieceAt(posPieceToPromote).getJoueurProprietaire()), posPieceToPromote);
                    notifyListeners(new ChessboardChangedEvent(null, null));
                }
                list.onRequestSuccess();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse == null)
                    list.onRequestError("erreur inconnue : " + error.toString());
                else if(error.networkResponse.statusCode == 401)
                    list.onRequestError("Non autorisé");
                else if(error.networkResponse.statusCode == 408) {
                    list.onRequestError("Temps écoulé!");
                    executeGameEnd(GameEndType.PERTE_TEMPS_ECOULE);
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.putAll(super.getHeaders());

                String key = "Authorization";
                String auth = "Basic " + Encoder.getCodeBase64(getRequestPassword());

                headers.put(key, auth);
                return headers;
            }
        };
        RequestSender.getInstance().addRequest(req);
    }

    public void getGameDetails(final GameManagerResponseListener list){
        String URI = GameConfiguration.getInstance().getServerURL() + "/game_details";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String player1 = null, player2 = null, round = null, location = null, enPassant = null;
                GameConfiguration.TimerFormat t = null;
                try {
                    player1 = response.getString("player1");
                    player2 = response.getString("player2");
                    round = response.getString("round");
                    location = response.getString("location");
                    enPassant = response.getString("enPassant");
                    JSONObject timerFormat = response.getJSONObject("timerFormat");
                    t = new GameConfiguration.TimerFormat(
                            timerFormat.getInt("time"),
                            timerFormat.getInt("increment"),
                            timerFormat.getInt("limit"),
                            timerFormat.getInt("overtime"),
                            timerFormat.getInt("overtimeIncrement")
                    );

                } catch (JSONException e) {
                    //error handling
                }
                GameConfiguration gc = GameConfiguration.getInstance();
                if (player1 != null)
                    gc.setParam(GameConfiguration.Parameter.nomJoueur1, player1);
                if (player2 != null)
                    gc.setParam(GameConfiguration.Parameter.nomJoueur2, player2);
                if (round != null)
                    gc.setParam(GameConfiguration.Parameter.rondeCourante, round);
                if (location != null)
                    gc.setParam(GameConfiguration.Parameter.locationOfGame, location);
                if (enPassant != null)
                    gc.setParam(GameConfiguration.Parameter.isEnPassantMode, enPassant.equals("yes"));
                if (t != null)
                    gc.setParam(GameConfiguration.Parameter.timerFormat, t);

                list.onRequestSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                list.onRequestError(error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.putAll(super.getHeaders());

                String key = "Authorization";
                String auth = "Basic " + Encoder.getCodeBase64(getRequestPassword());

                headers.put(key, auth);
                return headers;
            }
        };
        RequestSender.getInstance().addRequest(req);


    }

    //-------------------------PRIVATE METHODS------------------------------------------------------
    /**
     * Dispatch GameEvent e throughout all the listeners
     * @param e the GameEvent to be dispatched
     */
    protected void notifyListeners(IGameEvent e){
        for(GameManagerEventListener l : listeners){
            if(e instanceof ChessboardChangedEvent)
                l.onChessboardChanged((ChessboardChangedEvent)e);
            else if(e instanceof GameEndedEvent)
                l.onGameEnded((GameEndedEvent)e);
            else if(e instanceof GameStartedEvent)
                l.onGameStarted((GameStartedEvent) e);
            else if(e instanceof GameStatusChangedEvent)
                l.onGameStatusChanged((GameStatusChangedEvent) e);
            else if(e instanceof PlayerChangedEvent)
                l.onPlayerChanged((PlayerChangedEvent) e);
            else if(e instanceof PawnPromotionEvent)
                l.onPawnPromotion((PawnPromotionEvent) e);
        }
    }
    private void executeStartGame() {
        gameStatus = GameStatusType.normal;
        isGameInProgress = true;
        board.reset(); // start with a fresh board
        //notify the game has started
        notifyListeners(new GameStartedEvent());
        setPlayerCurrentlyPlaying(Player.getInstancePlayerWhite()); //par default le joueur blanc est le premier
    }
    protected void executeGameEnd(GameEndType reason){
        //on peut arreter la partie
        gameStatus = GameStatusType.end;
        playerCurrentlyPlaying = null;
        isGameInProgress = false;
        board.reset();
        notifyListeners(new GameEndedEvent(reason));
    }
    void doMove(final Player player,
                final ChessboardPosition source,
                final ChessboardPosition dest,
                final GameManagerResponseListener list) {

        //Construction de la requete pour bouger la piece
        String URI =
                GameConfiguration.getInstance().getServerURL() +
                        "/move/" + player.getNumeroJoueur().toString() + '/' +
                        source.toString() + "-" + dest.toString();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //move validated server-side

                //----------PIECE-ELIMINATED-----------------
                String pieceEliminated;
                Piece pieceElim = null;
                ChessboardPosition pos = null;
                //set piece Eliminated
                try {
                    pieceEliminated = response.getString("pieceEliminated");
                    if (pieceEliminated != null && !pieceEliminated.equals("X")) { //if there's really an eliminated piece
                        pos = new ChessboardPosition(pieceEliminated);
                        pieceElim = board.getPieceAt(pos).clonePiece();
                        board.setPiece(null, new ChessboardPosition(pieceEliminated)); //char digit -> int
                    }
                } catch (JSONException e) {}

                //----------PROMOTION-----------------
                String promotion = null;
                try{
                    promotion = response.getString("promotion");
                    if(promotion != null && promotion.equals("yes")) {
                        posPieceToPromote = dest;
                        notifyListeners(new PawnPromotionEvent(dest));
                    }
                } catch (JSONException e){}


                //----------STATE-----------------
                String state;
                try{
                    state = response.getString("state");
                    if(state != null) {
                        setGameStatus(GameStatusType.valueOf(state));
                    }
                }catch (JSONException e){}


                //----------MOVETYPE-----------------
                String moveType;
                try{
                    moveType = response.getString("moveType");
                    if(moveType != null && moveType.contains("castling")){
                        String[] tokens = moveType.split(" ");
                        if(tokens.length == 3) //legal castling move-> "moveType":"castling h1 f1"
                            board.movePiece(new ChessboardPosition(tokens[1]), new ChessboardPosition(tokens[2]));
                        notifyListeners(new ChessboardChangedEvent(null,null));//notify listener
                    }
                } catch (JSONException e){}

                //update board locally
                board.movePiece(source, dest);
                notifyListeners(new ChessboardChangedEvent(pieceElim,pos));//notify listener
                list.onRequestSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    list.onRequestError("erreur inconnue : " + error.toString());
                } else if (error.networkResponse.statusCode == 401) {
                    list.onRequestError("erreur : pas le bon mot de passe");
                } else if (error.networkResponse.statusCode == 403) {
                    list.onRequestError("erreur : pas le tour du joueur");
                } else if (error.networkResponse.statusCode == 406) {
                    list.onRequestError("erreur : déplacement illégal");
                } else if (error.networkResponse.statusCode == 408) {
                    list.onRequestError("erreur : temps écoulé, vous avez perdu!");
                    executeGameEnd(GameEndType.PERTE_TEMPS_ECOULE);
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.putAll(super.getHeaders());

                String key = "Authorization";
                String auth = "Basic " + Encoder.getCodeBase64(getRequestPassword());

                headers.put(key, auth);
                return headers;
            }
        };

        RequestSender.getInstance().addRequest(req);

    }


    //-------------------------PUBLIC GETTERS-------------------------------------------------------
    /**
     * @return true if there's a game currently going on
     */
    public boolean isGameInProgress() {
        return isGameInProgress;
    }
    /**
     * @return the current status of the game
     */
    public final GameStatusType getGameStatus(){
        return gameStatus;
    }
    /**
     * @return the Joueur currently playing
     */
    public final Player getPlayerCurrentlyPlaying(){
        return playerCurrentlyPlaying;
    }
     /**
     * @return the state of the current board. The Piece is null if there's no Piece at this
     * position.
     */
    public final ChessBoardObject getBoard() {
        return board;
    }
    /**
     * @return the total number of moves in this game
     */
    public Integer getTotalNoMove() {
        return totalMoveNo;
    }
    /**
     * @return the destination case of the last move
     */
    public String getLastMoveCase() {
        return lastMoveCase;
    }
    public String getRequestPassword(){
        return (String) GameConfiguration.getInstance().getParam(GameConfiguration.Parameter.secretCode);
    }

    //-------------------------PRIVATE SETTERS------------------------------------------------------
    protected void setGameStatus(GameStatusType status) {
        GameStatusType old = getGameStatus();
        if(status == old || status == null)
            return; // no need to change
        if(old == GameStatusType.waiting_for_gamestart && status == GameStatusType.normal)
            executeStartGame();
        if(status == GameStatusType.waiting_for_gamestart ||
                status == GameStatusType.checkmate ||
                status == GameStatusType.end)
            executeGameEnd(GameEndType.PERTE_ECHEC_ET_MAT);
        if(status == GameStatusType.stalemate)
            executeGameEnd(GameEndType.PERTE_PARTIE_NULLE);
        this.gameStatus = status;
        notifyListeners(new GameStatusChangedEvent(old, getGameStatus()));
    }
    protected void setPlayerCurrentlyPlaying(Player j){
        if(playerCurrentlyPlaying == j || j == null)//same Joueur
            return;
        this.playerCurrentlyPlaying = j;
        notifyListeners(new PlayerChangedEvent(this.playerCurrentlyPlaying));
    }
    protected void setTotalMoveNo(Integer totalMoveNo) {
        this.totalMoveNo = totalMoveNo;
    }
    protected void setLastMoveCase(String lastMoveCase) {
        this.lastMoveCase = lastMoveCase;
    }
}

