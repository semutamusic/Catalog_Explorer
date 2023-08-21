# Library Toolbox

This Android app is a personal project, meant to both stretch my programming muscles and act as a tool to those who find themselves in a library, either as a patron or an employee.

## Functions

This app currently has four main functions, each accessible from the main menu

- A Guessing Game, which lets the user guess what topic is covered in a randomly given Library of Congress code. The user selects one of three possibilities, and is scored on a correct-to-total basis. The idea is for the user to scan the shelves of their local library and make an informed guess on the topic based on the library's available books in that section. The score is non-persistant and will reset each time the game is opened.
- A Random Library of Congress Code Generator, that generates a random LOC code each time the Generate button is pressed. Each of the four sections of the code (Subclass, Index, Author and Year) can be manually input and locked for future generations, though the only one that affects the generation of any other line is the Subclass (which limits the Index to a valid range). Any set locks be be reset with the Reset Locks button on the bottom right.
- A Random Fiction Author Generator, which randomly displays the name of a popular fiction author from a list of over 500 possibilities. For added variation, an ink splotch will randomly black out all but the first three letters of the displayed name, encouraging the user to expand their search beyond the most popular authors.
- A Library of Congress Index, which allows the user to easily browse all of the Library of Congress subclasses, scrolling first to select a class, then to view the different subclasses.

The main menu also contains Help buttons to give basic descriptions of each of these functions.

## Structure

Each Activity in this app naturally uses a dedicated class to manage creation and user input. Some Activities also use simple helper classes to manage more complicated data organization. In addition, a Library of Congress Code Manager and a Fiction Author Manager were written to manage the generation and manipulation of LOC codes and random author names between the different activities.

## TODO

- Redesign the LOC Manager's current method of storing valid LOC codes (a HashMap String structure) into a simpler, more efficient Tree structure.
- Reorganize the Guessing Game's results timer into a single method, possibly an animation, instead of the currently spread out structure.
- Add a Library of Congress Code Finder game, where the user is awarded points for finding books in their local library that match a randomly generated Library of Congress code as closely as possible. More points would be given based on closeness to each of the four sections of the code, and the titles of any found books would be able to be recorded and viewed at a later time.
- Add an ARG-type puzzle game? ¯\\\_(ツ)\_/¯
- Make sure any visual assets are within fair copyright usage.
- Appropriately comment code.
