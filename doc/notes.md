## Done
- get started!
- minimum viable React wrapper
- citrus + React hooks: events + effects, like Elm. wow!
- playground: editor + results.

## Next
- shadow.remote is exactly what I wanted for client/server eval + storing results to inspect
- rework playground to use that!

## Later
- Server
  - Edit and save
  - Eval
  - Playground
- Client
  - Edit and save
  - Eval
  - Playground
  - Plain cljs watch: if I refresh, use latest saved client.
- Console output
- File/namespace browser
- "Code Bubbles"
- Visual results.
- Visual editor tools.
- Fully consider hot-loading: "other" client windows (like figwheel); ns tracker

## Notes
codemirror (as lighttable's inline bits - shadow's ui has this)
client eval IN client (eval-soup) - no round trip, autocomplete js objects
not sure whether I like figwheel or shadow for this one.