package com.bbn.bue.common.files;

import com.bbn.bue.common.collections.MapUtils;
import com.bbn.bue.common.symbols.Symbol;
import com.bbn.bue.common.symbols.SymbolUtils;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Merges DocIDToFileMaps together.
 */
public class MergeDocIDToFileMaps {

  private static final Logger log = LoggerFactory.getLogger(MergeDocIDToFileMaps.class);

  private MergeDocIDToFileMaps() {
    throw new UnsupportedOperationException();
  }

  public static void main(String[] argv) {
    // we wrap the main method in this way to
    // ensure a non-zero return value on failure
    try {
      trueMain(argv);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static void trueMain(String[] argv) throws IOException {
    if (argv.length != 2) {
      System.err.println("usage: MergeDocIdToFileMaps inputListOfMaps outputMap:\n"
          + "\tinputListOfMaps: a file with one filename per line of file maps to merge\n"
          + "\toutputMap: a file to write the merged map to\n");
      System.exit(1);
    }
    final File listOfMaps = new File(argv[0]);
    final File outputMap = new File(argv[1]);

    final Map<Symbol, File> mergedMap = Maps.newHashMap();
    for (final File mapFile : FileUtils
        .loadFileList(Files.asCharSource(listOfMaps, Charsets.UTF_8))) {
      final ImmutableMap<Symbol, File> mapFromFile = FileUtils.loadSymbolToFileMap(
          Files.asCharSource(mapFile, Charsets.UTF_8));
      log.info("Loaded {} file mappings from {}", mapFromFile.size(), mapFile);
      for (final Map.Entry<Symbol, File> mapEntry : mapFromFile.entrySet()) {
        final File curMapping = mergedMap.get(mapEntry.getKey());
        if (curMapping == null) {
          mergedMap.put(mapEntry.getKey(), mapEntry.getValue());
        } else if (!curMapping.equals(mapEntry.getValue())) {
          throw new RuntimeException(mapEntry.getKey() + " is mapped to " + mapEntry.getValue()
              + " in " + mapFile + " but has been mapped to " + curMapping + " in a previously "
              + "processed file");
        } else {
          // do nothing - it's fine to repeat the same mapping in multiple files
        }
      }
      mergedMap.putAll(mapFromFile);
    }
    outputMap.getParentFile().mkdirs();

    final ImmutableMap<Symbol, File> sortedMergedMap = MapUtils.copyWithKeysSortedBy(mergedMap,
        SymbolUtils.byStringOrdering());
    ;
    log.info("Wrote map of {} files to {}", sortedMergedMap.size(), outputMap);
    FileUtils.writeSymbolToFileMap(sortedMergedMap, Files.asCharSink(outputMap, Charsets.UTF_8));
  }
}
