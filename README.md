# Catalog Explorer

This Android app is a personal project, meant to both stretch my programming muscles and act as a tool to those who find themselves in a library, either as a patron or an employee.

## Catalogs

This app currently features three different catalogs, each of which are commonly used in public libraries to organize their collections.

- **Library of Congress**, taken from the *Library of Congress' FreeLCC website*
- **Dewey Decimal System**, taken from *OCLC's website*
- **Classical Authors**, about 1,000 of the most popular authors on *Project Gutenberg*

## Functions

This app currently has five main functions, each accessible from the main menu. All catalogs can use all functions.

- An informational **Help** menu, describing the basics of the selected catalog, how to read its codes, and where the information comes from
- A **Browser**, showing the catalog in a tiered list. Tap an topics to expand it, displaying all of the sub-topics
- A **Code Search** menu, taking a valid catalog code as input and displaying what topic books cataloged under that code cover
- A **Keyword Search** menu, taking any number of words as input and displaying a list of catalog codes that mention that topic
- A **Guessing Game**, displaying a random catalog code, and letting you pick which one of three topics you think it covers
	- Good for practicing with an unfamiliar catalog, though it helps to have shelves of the cataloged books in front of you

## Structure

Each Activity in this app naturally uses a dedicated class to manage creation and user input.

The three catalogs are all managed by a Manager class, all of which inherit from a central CatalogManager. Each manager stores its catalog in a node-based tree structure using a class inherited from a central CatalogNode class. Each node has two instances of a class inheriting from a CatalogCode class, representing the start and end of the range of CatalogCodes that CatalogNode covers. Every CatalogCode has a number of fields inheriting from CatalogFields, which can represent Strings, Cutter numbers, years, and more.

The five basic functions are all catalog-agnostic; they can handle any class as long as it inherits from the abstract Catalog classes (CatalogManager, CatalogNode, etc.)

## TODO

- [x] Redesign the LOC Manager's current method of storing valid LOC codes (a HashMap String structure) into a simpler, more efficient Tree structure.
- [ ] Add more detail to the three Catalogs
	- [ ] The Library of Congress catalog has data from FreeLCC's Outline pages, but more detail can be added from the Text pages
	- [ ] The Dewey Decimal System catalog only stores topics for whole numbers. More detail can be added from the decimal field
	- [ ] The Classical Author catalog could store genres that the authors are associated with, as well as more recent authors (Project Gutenberg mostly lists authors from almost 100 years ago)
- [ ] Reorganize Guess Game's result timer into a single method, possibly an animation, instead of the currently spread out structure.
- [ ] Proofread the extensive CSVs that store the catalog data. Most of the information was processed using Python scripts, and contains typos or misaligned ranges, occasionally causing topics to get grouped improperly.
- [ ] Manage de-instantiation of catalogs. Currently, all three catalogs are loaded into RAM when the app starts. It uses about 100MB of RAM, which is managable, but only instantiating the catalog currently in use would probably be better.
- [ ] Add a Library of Congress Code Finder game, where the user is awarded points for finding books in their local library that match a randomly generated Library of Congress code as closely as possible. More points would be given based on closeness to each of the four sections of the code, and the titles of any found books would be able to be recorded and viewed at a later time.
- [ ] Add an ARG-type puzzle game? ¯\\\_(ツ)\_/¯
- [x] Make sure any visual assets are within fair copyright usage.
- [x] Appropriately comment code.
