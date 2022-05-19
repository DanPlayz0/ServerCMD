package me.danplayz.servercommand.commands;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import me.danplayz.servercommand.Core;

import java.util.Locale;
import com.google.common.base.Predicate;

import java.io.IOException;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.Collections;
import java.util.Map;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.api.plugin.Command;

public class CommandServer extends Command implements TabExecutor
{
    public CommandServer() {
        super("server", "bungeecord.command.server", new String[0]);
    }
    
    @SuppressWarnings("deprecation")
	public void execute(final CommandSender sender, final String[] args) {
        final Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
        if (args.length == 0) {
            if (sender instanceof ProxiedPlayer) {
                final ProxiedPlayer player = (ProxiedPlayer)sender;
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Core.config.getConfig().getString("usage").replace("{server}", player.getServer().getInfo().getName())));
            }
            final ComponentBuilder serverList = new ComponentBuilder("").appendLegacy(ChatColor.translateAlternateColorCodes('&', Core.config.getConfig().getString("list")));
            boolean first = true;
            for (final ServerInfo server : servers.values()) {
            	String sstatus = Core.config.getConfig().getString("messages.offline");
            	boolean bstatus = false;
            	try {
                	Socket s = new Socket();
                	s.connect(server.getAddress(), 15); //good timeout is 10-20
                	s.close();
                	sstatus = Core.config.getConfig().getString("messages.online");
                	bstatus = true;
            	} catch (UnknownHostException e){
            		sstatus = Core.config.getConfig().getString("messages.offline");
            		bstatus = false;
            	} catch (IOException e) {
            		sstatus = Core.config.getConfig().getString("messages.offline");
            		bstatus = false;
            	}
            	if(Core.config.getConfig().getBoolean("hideoffline") == true) {
            		if(bstatus == true) {
            			if (server.canAccess(sender)) {
    	                    final TextComponent serverTextComponent = new TextComponent(first ? server.getName() : (Core.config.getConfig().getString("messages.seperator") + server.getName()));
    	                    final int count = server.getPlayers().size();
    	                    serverTextComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', Core.config.getConfig().getString("hover").replace("{count}", String.valueOf(count)).replace("{status}", sstatus))).create()));
    	                    serverTextComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Core.config.getConfig().getString("click").replace("{server}", server.getName())));
    	                    serverList.append(serverTextComponent);
    	                    first = false;
    	                }
            		}
            	} else {
            		if (server.canAccess(sender)) {
	                    final TextComponent serverTextComponent = new TextComponent(first ? server.getName() : (Core.config.getConfig().getString("messages.seperator") + server.getName()));
	                    final int count = server.getPlayers().size();
	                    serverTextComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', Core.config.getConfig().getString("hover").replace("{count}", String.valueOf(count)).replace("{status}", sstatus))).create()));
	                    serverTextComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Core.config.getConfig().getString("click").replace("{server}", server.getName())));
	                    serverList.append(serverTextComponent);
	                    first = false;
	                }
            	}
            }
            sender.sendMessage(serverList.create());
        }
        else {
            if (!(sender instanceof ProxiedPlayer)) {
                return;
            }
            final ProxiedPlayer player = (ProxiedPlayer)sender;
            final ServerInfo server2 = servers.get(args[0]);
            if (server2 == null) {
                player.sendMessage(ProxyServer.getInstance().getTranslation("no_server", new Object[0]));
            }
            else if (!server2.canAccess((CommandSender)player)) {
                player.sendMessage(ProxyServer.getInstance().getTranslation("no_server_permission", new Object[0]));
            }
            else {
                player.connect(server2, ServerConnectEvent.Reason.COMMAND);
            }
        }
    }
    
	public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        return ((args.length > 1) ? Collections.emptyList() : Iterables.transform(Iterables.filter(ProxyServer.getInstance().getServers().values(), new Predicate<ServerInfo>() {
            private final String lower = (args.length == 0) ? "" : args[0].toLowerCase(Locale.ROOT);
            
            public boolean apply(final ServerInfo input) {
                return input.getName().toLowerCase(Locale.ROOT).startsWith(this.lower) && input.canAccess(sender);
            }
        }), new Function<ServerInfo, String>() {
            public String apply(final ServerInfo input) {
                return input.getName();
            }
        }));
    }
}