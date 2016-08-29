package me.philipiv.pastebook;

import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import net.md_5.bungee.api.ChatColor;

public class DownloadCommand implements CommandExecutor {
	private static PasteBook plugin;
	
	public DownloadCommand(PasteBook plugin) {
		DownloadCommand.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You must be a player to use this command");
			return true;
		}
		Player player = (Player) sender;
		
		if (args.length != 1) {
			sender.sendMessage(ChatColor.RED + "Incorrect format. Correct use is\n/pbupload <pastebin id>");
			return true;
		}
		
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		try {
			BookMeta bm = plugin.download(args[0]);
			bm.getAuthor(); //simple way to throw NPE if bm returned null
			book.setItemMeta(bm);
			player.getInventory().addItem(book);
			player.sendMessage(ChatColor.GREEN + "Book downloaded!");
		}
		catch (IOException e) {
			player.sendMessage(ChatColor.RED + "Error communicating with Pastebin. Check to make sure your paste is valid, and try again later.");
		}
		catch (NullPointerException e) {
			player.sendMessage(ChatColor.RED + "Error parsing book. Check to make sure the book is from this plugin, and try again.");
		}
		catch (Exception e) {
			e.printStackTrace();
			player.sendMessage(ChatColor.RED + "There was an error. Please contact your server admin.");
		}
		return true;
	}

}
