package org.camunda.community.process_test_coverage.sonar

import org.sonar.api.utils.WildcardPattern
import org.sonar.api.utils.log.Logger
import org.sonar.api.utils.log.Loggers
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


object WildcardPatternFileScanner {

    private val LOG: Logger = Loggers.get(WildcardPatternFileScanner::class.java)
    private const val SEARCH_MAX_DEPTH = 64
    private const val PATH_MATCHER_SPECIAL_CHAR = "*?"

    fun scan(baseDirectory: Path, patternPath: String): List<Path> {
        val unixLikePatternPath = toUnixLikePath(patternPath)
        val specialCharIndex = indexOfMatcherSpecialChar(unixLikePatternPath)
        if (specialCharIndex == -1) {
            return scanNonWildcardPattern(baseDirectory, unixLikePatternPath)
        } else {
            // For performance reason, we don't want to scan recursively all files in baseDirectory
            // when patternPath start with "none wildcard" subfolder names. For example,
            // scanWildcardPattern("/base", "sub1/sub2/**/file*.xml") is converted into
            // scanWildcardPattern("/base/sub1/sub2", "**/file*.xml")
            val additionalBaseDirectoryPart = unixLikePatternPath.lastIndexOf('/', specialCharIndex)
            if (additionalBaseDirectoryPart != -1) {
                val additionalBaseDirectory: Path =
                    toFileSystemPath(unixLikePatternPath.substring(0, additionalBaseDirectoryPart + 1))
                val remainingWildcardPart = unixLikePatternPath.substring(additionalBaseDirectoryPart + 1)
                val moreSpecificBaseDirectory: Path = baseDirectory.resolve(additionalBaseDirectory)
                return scanWildcardPattern(moreSpecificBaseDirectory, remainingWildcardPart)
            } else {
                return scanWildcardPattern(baseDirectory, unixLikePatternPath)
            }
        }
    }

    private fun scanNonWildcardPattern(baseDirectory: Path, unixLikePath: String): List<Path> {
        val path: Path = baseDirectory.resolve(toFileSystemPath(unixLikePath))
        return if (Files.isRegularFile(path)) {
            listOf(path)
        } else emptyList()
    }

    private fun scanWildcardPattern(baseDirectory: Path, unixLikePatternPath: String): List<Path> {
        if (!Files.exists(baseDirectory)) {
            return emptyList()
        }
        try {
            val absoluteBaseDirectory: Path = baseDirectory.toRealPath()
            if (absoluteBaseDirectory == absoluteBaseDirectory.root) {
                throw IOException("For performance reason, wildcard pattern search is not possible from filesystem root")
            }
            val paths: MutableList<Path> = mutableListOf()
            val matcher = WildcardPattern.create(toUnixLikePath(absoluteBaseDirectory.toString()) + "/" + unixLikePatternPath)
            Files.walk(absoluteBaseDirectory, SEARCH_MAX_DEPTH).use { stream ->
                stream
                    .filter(Files::isRegularFile)
                    .filter { path -> matcher.match(toUnixLikePath(path.toString())) }
                    .forEach(paths::add)
            }
            return paths
        } catch (e: IOException) {
            LOG.error(
                "Failed to get Process Test Coverage report paths: Scanning '" + baseDirectory + "' with pattern '" + unixLikePatternPath + "'" +
                        " threw a " + e.javaClass.simpleName + ": " + e.message
            )
            return emptyList()
        } catch (e: RuntimeException) {
            LOG.error(
                ("Failed to get Process Test Coverage report paths: Scanning '" + baseDirectory + "' with pattern '" + unixLikePatternPath + "'" +
                        " threw a " + e.javaClass.simpleName + ": " + e.message)
            )
            return emptyList()
        }
    }

    private fun toUnixLikePath(path: String): String {
        return if (path.indexOf('\\') != -1) path.replace('\\', '/') else path
    }

    private fun toFileSystemPath(unixLikePath: String): Path {
        return Paths.get(unixLikePath.replace('/', File.separatorChar))
    }

    private fun indexOfMatcherSpecialChar(path: String): Int {
        for (i in path.indices) {
            if (PATH_MATCHER_SPECIAL_CHAR.indexOf(path[i]) != -1) {
                return i
            }
        }
        return -1
    }
}