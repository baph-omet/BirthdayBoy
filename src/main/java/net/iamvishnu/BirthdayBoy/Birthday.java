package net.iamvishnu.BirthdayBoy;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public class Birthday {
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	public UUID PlayerUUID;
	public LocalDate Date;
	public LocalDate LastClaimedDate;

	public Birthday(LocalDate date, UUID uuid, LocalDate lastClaimed) {
		Date = date;
		PlayerUUID = uuid;
		LastClaimedDate = lastClaimed;
	}

	public String ToReadableString() {
		return String.format("%s %s, %s", Date.getMonth().name(), DayToOrdinal(Date.getDayOfMonth()),
				Integer.toString(Date.getYear()));
	}

	public String LastClaimedToReadableString() {
		return String.format("%s %s, %s", LastClaimedDate.getMonth().name(),
				DayToOrdinal(LastClaimedDate.getDayOfMonth()), Integer.toString(LastClaimedDate.getYear()));
	}

	public void Save() {
		ArrayList<Birthday> all = GetAllBirthdays();
		boolean found = false;
		for (int i = 0; i < all.size(); i++) {
			Birthday b = all.get(i);
			if (b.PlayerUUID.compareTo(PlayerUUID) == 0) {
				found = true;
				all.set(i, this);
				break;
			}
		}

		if (!found) all.add(this);

		BirthdayBoy.BirthdayConfig.getConfig().set("birthdays", BDayListToString(all));
		BirthdayBoy.BirthdayConfig.save();
	}

	public void Delete() {
		int foundIndex = -1;
		ArrayList<Birthday> all = GetAllBirthdays();
		for (int i = 0; i < all.size() && foundIndex < 0; i++)
			if (all.get(i).PlayerUUID.compareTo(PlayerUUID) == 0)
				foundIndex = i;

		if (foundIndex >= 0) {
			all.remove(foundIndex);
			BirthdayBoy.BirthdayConfig.getConfig().set("birthdays", BDayListToString(all));
			BirthdayBoy.BirthdayConfig.save();
		}
	}

	public boolean IsToday() {
		LocalDate now = LocalDate.now();
		return now.getDayOfMonth() == Date.getDayOfMonth() && now.getMonth() == Date.getMonth();
	}

	public boolean ClaimedToday() {
		LocalDate now = LocalDate.now();
		return LastClaimedDate != null && now.compareTo(LastClaimedDate) <= 0;
	}

	private static String DayToOrdinal(int day) {
		String d = Integer.toString(day);
		switch (d.charAt(d.length() - 1)) {
		case '1':
			if (day == 11)
				return "11th";
			return d + "st";
		case '2':
			if (day == 12)
				return "12th";
			return d + "nd";
		case '3':
			if (day == 13)
				return "13th";
			return d + "rd";
		default:
			return d + "th";
		}
	}

	public static ArrayList<Birthday> GetAllBirthdays() {
		ArrayList<Birthday> bdays = new ArrayList<Birthday>();
		List<String> all = BirthdayBoy.BirthdayConfig.getConfig().getStringList("birthdays");
		for (String s : all) {
			String[] sarr = s.toString().split(";");
			Birthday b = null;
			try {
				UUID uuid = UUID.fromString(sarr[0]);
				LocalDate d = ParseDate(sarr[1]);
				LocalDate dc = null;
				if (sarr.length > 2 && !sarr[2].isBlank()) dc = ParseDate(sarr[2]);
				b = new Birthday(d, uuid, dc);
			} catch (Exception e) {
				BirthdayBoy.serverLog.warning("Error parsing data for one of the dates.\n" + e.toString());
			}

			if (b != null)
				bdays.add(b);
		}
		return bdays;
	}

	public static Birthday GetBirthday(Player p) {
		return GetBirthday(p.getUniqueId());
	}
	public static Birthday GetBirthday(UUID uuid) {
		for (Birthday b : GetAllBirthdays())
			if (b.PlayerUUID.compareTo(uuid) == 0)
				return b;
		return null;
	}

	public static LocalDate ParseDate(String text) {
		try {
			return LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyyMMdd"));
		} catch (DateTimeParseException e) {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return String.join(";", new String[] { 
				PlayerUUID.toString(),
				Date.format(formatter),
				LastClaimedDate != null ? LastClaimedDate.format(formatter) : ""
		});
	}
	
	public static List<String> BDayListToString(List<Birthday> list) {
		ArrayList<String> newList = new ArrayList<String>();
		for (Birthday b : list) newList.add(b.toString());
		return newList;
	}
}
