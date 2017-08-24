var eval = function(code) {
    return engine.eval(code);
};

// Setup basic global server shortcuts.
var org = Packages.org;
var net = Packages.net;
var Core = net.kineticraft.lostcity.Core;
var plugin = Core.instance;
var Bukkit = org.bukkit.Bukkit;
var server = Bukkit.server; // Setup 'server' keyword

// Basic IO.
var console = {};
console.log = server.logger.info;
console.warn = server.logger.warn;

var ChatColor = org.bukkit.ChatColor; // Setup ChatColor keyword.
var Sound = org.bukkit.Sound;

// Setup schedulers
var toTicks = function (millis) {
    return Math.ceil(millis / 50);
};

var setTimeout = function (callback, delayMS) {
    return server.scheduler.runTaskLater(plugin, callback, toTicks(delayMS));
};

var setInterval = function (callback, intervalMS) {
    var delayMS = toTicks(intervalMS);
    return server.scheduler.runTaskTimer(plugin, callback, delayMS, delayMS);
};

var toNMS = function (item) {
    return new net.kineticraft.lostcity.item.ItemWrapper(item).getNMSCopy();
}
