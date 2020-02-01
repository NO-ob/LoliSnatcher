# Loli Snatcher 
[![github-small](https://www.gnu.org/graphics/gplv3-with-text-136x68.png)](https://www.gnu.org/licenses/gpl-3.0)

A Program To Search and Snatch Images From Boorus

When running for the first time open the settings window and press save to save the default settings to a file.

File Name Variables
$SEARCH[n] - Will take n variables from the search tags
$TAGS[n] - Will take n tags from the fetched image
$ID - Post id
$HASH - File hash
$EXT - File extension

![github-small](https://i.imgur.com/s0gX1N0.png)

Tasks
- [x] Search and retrieve data from gelbooru
- [x] View image previews
- [x] Load new image previews on scroll
- [x] View Tags and Full Sized Image in a new window
- [x] Batch download Images from gelbooru
- [x] Add support for danbooru
- [x] Add settings window/manager
    - [x] Amount of images to fetch in one go
    - [x] Save location
    - [x] More boorus
    - [x] Timeout between Snatching
    - [x] Default Tags
    - [x] Custom File Names
- [ ] Design UI
    - [ ] Fix Image window layout
    - [ ] Fix Main window layout
    - [ ] Tidy UI
    - [ ] Themes
        - [ ] Neir automata menu style maybe
- [ ] Refactoring
    - [ ] Use XML Parser instead of string functions and regex in the booruHandlers and Snatcher
    - [ ] Make the snatcher remove offending item from the arraylist during an exception and then continue
- [ ] Webm and Gif support

