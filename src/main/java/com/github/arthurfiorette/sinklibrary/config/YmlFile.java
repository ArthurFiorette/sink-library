package com.github.arthurfiorette.sinklibrary.config;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * A interface to represents any custom .yml file loaded from this plugin
 *
 * @author https://github.com/ArthurFiorette/sink-library/
 */
public interface YmlFile {
  /**
   * Reloads the configuration from the located file.
   */
  void load();

  void setup(String defaultPath, boolean replace);

  /**
   * @return the file configuration from this yml instance
   */
  FileConfiguration getConfig();

  /**
   * @return the file name
   */
  String getName();

  /**
   * @return this yml file as a File object
   */
  File asFile();

  /**
   * Return get file configuration and reload before return if it's needed.
   *
   * @param reload true if needs to reload.
   *
   * @return the file configuration from this yml instance
   */
  default FileConfiguration getConfig(boolean reload) {
    if (reload) {
      this.load();
    }
    return this.getConfig();
  }
}