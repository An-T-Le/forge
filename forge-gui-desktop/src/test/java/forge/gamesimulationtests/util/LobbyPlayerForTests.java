package forge.gamesimulationtests.util;

import forge.LobbyPlayer;
import forge.game.Game;
import forge.game.player.IGameEntitiesFactory;
import forge.game.player.Player;
import forge.game.player.PlayerController;
import forge.gamesimulationtests.util.playeractions.PlayerActions;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Default harmless implementation for tests.
 * Test-specific behaviour can easily be added by mocking (parts of) this class.
 */
public class LobbyPlayerForTests extends LobbyPlayer implements IGameEntitiesFactory {
	private final Map<Player, PlayerControllerForTests> playerControllers;
	private final PlayerActions playerActions;
	
	public LobbyPlayerForTests( String name, PlayerActions playerActions ) {
		super( name );
		playerControllers = new HashMap<Player, PlayerControllerForTests>();
		this.playerActions = playerActions;
	}
	

	@Override
	public Player createIngamePlayer( Game gameState ) {
		Player dummyPlayer = new Player( getName(), gameState );
		dummyPlayer.setFirstController( createControllerFor( dummyPlayer ) );
		return dummyPlayer;
	}

	@Override
	public PlayerController createControllerFor( Player player ) {
		if( !playerControllers.containsKey( player ) ) {
			PlayerControllerForTests dummyPlayerControllerForTests = new PlayerControllerForTests( player.getGame(), player, this );
			dummyPlayerControllerForTests.setPlayerActions( playerActions );
			playerControllers.put( player, dummyPlayerControllerForTests );
		}
		return playerControllers.get( player );
	}

	@Override
	public void hear( LobbyPlayer player, String message ) {
		//Do nothing
	}
	
	public PlayerControllerForTests getPlayerController() {
		if( playerControllers.size() == 1 ) {
			return playerControllers.values().iterator().next();
		}
		throw new IllegalStateException( "Can't determine correct controller " + StringUtils.join( playerControllers.entrySet(), ", " ) );
	}
}
