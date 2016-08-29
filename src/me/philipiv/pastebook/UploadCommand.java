package me.philipiv.pastebook;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class UploadCommand implements CommandExecutor {
	private static PasteBook plugin;
	
	public UploadCommand(PasteBook plugin) {
		UploadCommand.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You must be a player to use this command");
			return true;
		}
		
		Player player = (Player) sender;
		ItemStack is = player.getInventory().getItemInMainHand();
		if (is.getType() == Material.BOOK_AND_QUILL) {
			sender.sendMessage(ChatColor.RED + "You must finish and sign the book before it can be uploaded");
			return true;
		}
		else if (is.getType() != Material.WRITTEN_BOOK) {
			sender.sendMessage(ChatColor.RED + "You don't have a book in your hand to upload");
			return true;
		}
		
		String response = plugin.upload((BookMeta) is.getItemMeta());
		if (response.equals("")) {
			sender.sendMessage(ChatColor.RED + "There was a problem uploading your book. Please try again later.");
		}
		else {
			sender.sendMessage(ChatColor.GREEN + "Upload success! Your book can be found at: " + response);
		}
		return true;
	}

}
