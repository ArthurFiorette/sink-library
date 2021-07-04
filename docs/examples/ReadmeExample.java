package examples;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.arthurfiorette.sinklibrary.core.BasePlugin;
import com.github.arthurfiorette.sinklibrary.core.SinkPlugin;
import com.github.arthurfiorette.sinklibrary.interfaces.BaseComponent;
import com.github.arthurfiorette.sinklibrary.listener.SinkListener;

public class ReadmeExample extends SinkPlugin {

  @Override
  public void enable() throws Exception {}

  @Override
  public void disable() throws Exception {}

  @Override
  protected BaseComponent[] components() {
    return new BaseComponent[] { new MyListener(this) };
  }
}

// A simple class to be our listener
class MyListener extends SinkListener {

  public MyListener(final BasePlugin owner) {
    super(owner);
  }

  @Override
  @EventHandler
  public void onPlayerJoin(final PlayerJoinEvent event) {
    // On every player join event, get the player and send a message
    event.getPlayer().sendMessage("Hello World!");
  }
}
