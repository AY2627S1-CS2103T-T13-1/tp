# Course review lenses and notes index

Review guidance synthesized from the user's CS2103T course notes, with an index for consulting the original material when available.

## Design and code quality

[Course principles](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/principles.html#principles)

| Lens | Evidence to look for |
| --- | --- |
| SRP / SoC | Independent reasons to change mixed in one class/component. |
| OCP | A real extension forces unrelated central logic to change. |
| LSP | A subtype rejects inputs or breaks guarantees promised by its parent. |
| ISP / DIP | Clients depend on irrelevant operations; policy is tied to concrete implementation details. |
| Law of Demeter | Knowledge of another object's internal structure creates coupling; a fluent call chain alone is insufficient evidence. |
| DRY / YAGNI | The same knowledge has competing definitions, or speculative capability adds avoidable complexity. |

From Weeks 5, 7, and 9: prefer cohesive modules, explicit contracts, limited coupling, and meaningful abstraction levels. SLAP means readable high-level vocabulary, not mechanically extracting every block. Roughly 30-line methods are a readability heuristic, not an automatic correctness defect. Explain why nesting, duplication, magic values, or an abstraction makes this particular change harder to understand or maintain.

From Weeks 5, 6, and 10: assertions check programmer assumptions and must not perform essential work; recoverable user/environment failures need validation or exceptions. Check mutable aliases, compulsory associations, referential integrity, swallowed exceptions, and logs that hide useful context or expose sensitive data. Use the smallest failing example to trace the origin, then verify the fix with a regression test when a fix is requested.

## Testing

[Course testing](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/testing.html#testing)

Unit tests check units, integration tests check interaction contracts, system tests check the specification, and acceptance tests check fitness for target users. Combine scripted critical workflows with exploratory probes. Isolate time/storage dependencies where useful. Measure the relevant coverage criterion; executed code alone does not establish correct assertions or complete requirements.

From Weeks 8 and 10: derive equivalence partitions from the contract, including receiver state, arguments, and relevant external state. Include valid/invalid partitions and meaningful boundary and interior values. Vary one invalid factor at a time where useful so another failure does not mask the target check. Java type-impossible inputs are not missing unit cases, but malformed text can still reach a command parser. Consider empty/singleton/populated collections, missing/repeated prefixes, whitespace/case, filtered indices, and save/reload sequences when the feature supports them. Record setup, input, expected outcome, and observed outcome.

The Week 8 note's claim that full path coverage implies “bug free” is unsafe: missing requirements, incorrect oracles, and untested data can remain. Do not propagate it.

## Documentation and UML

Check that user stories connect a role, goal, and benefit; use cases describe actor/system interactions with meaningful extensions; and NFRs have a clear, measurable scope. Architecture diagrams should emphasize component responsibilities and explain unfamiliar symbols.

For UML findings, inspect the actual diagram and distinguish misleading notation from purposeful abstraction or optional omissions. Common checks:

* Class diagrams: solid associations, dashed dependencies, hollow generalization triangles pointing to the parent, and aggregation/composition diamonds at the whole end. Missing multiplicity means unspecified, not automatically one. Object diagrams show underlined `instance:Class` labels and concrete links rather than class multiplicities.
* Sequence diagrams: follow message order, lifelines, and activation/creation/destruction against the described interaction; distinguish calls from dashed returns. Avoid demanding every low-level call or optional return.
* Activity diagrams: distinguish guarded alternatives and merges from parallel forks and joins. Check that paths and guards match the intended workflow.

## Optional source-note lookup

Consult this index only if the user supplies the original notes. Otherwise skip it and continue with the bundled guidance; do not request or search for a notes folder as a prerequisite. Paths are relative to that optional vault. Titles alone do not establish that a topic has been written up; some Week 4, 9, and 11 sections are unfinished.

| Topic | Note and useful section/search term |
| --- | --- |
| Java conventions | `Week 3/Java Coding Standard Summary.md` |
| PR workflow and Javadoc | `Week 3/Week 3 - Branching, Pull Requests, Javadoc, Code Quality and Testing.md`: `Reviewing Pull Requests`, `JavaDoc` |
| Readability and assumptions | `Week 5/Week 5 - Requirements, Code Quality, Refactoring, Assertions and Streams.md`: §§5.4–5.6; `Week 5/Week 5 Briefing.md`: `SLAP` |
| Debugging, logging, sequence UML | `Week 6/Week 6 - Sequence & Architecture Diagrams, Debugging, Intermediate IDE Features and Logging.md`: §§6.1, 6.3–6.4 |
| Requirements and coupling | `Week 7/Week 7 - Requirements, Design, Integration Approaches and Project Management.md`: `Use Cases`, `Design: Fundamentals` |
| Test levels and coverage | `Week 8/Week 8 - Drawing Diagrams and Testing.md`: §§8.4–8.5 |
| Architecture and principles | `Week 9/Week 9 - Conceptual Class, Activity, Architecture Diagrams; SDLC and Writing Developer Documents.md`: §§9.4–9.5 |
| Defensive coding and test design | `Week 10/Week 10 - Design Patterns, Defensive Programming and Test Cases.md`: §§10.2–10.5; `Week 10/Week 10 Briefing.md` |

