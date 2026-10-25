# Code and PR issue catalogue

Use these patterns to investigate a review, not as findings to repeat automatically. Rule details live in [standards](standards.md); design/testing rationale lives in [course notes](course-notes.md). The rows below are review examples and suggested remedies, not an official grading checklist or claims about a particular PR.

## Rating policy

Use the impact definitions in [PE rules](pe.md#reporting-rules). Select one level for an actual finding and explain affected users/readers, frequency, disruption, and recovery. A catalogue range is only a starting point; a crash or data loss is not automatically `High`.

The [official PE scope](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-pe.html#tp-practical-exam-pe) excludes code quality and unrelated source/site files. For code, test, or Git violations without a demonstrated product/UG/DG defect, write `PE severity: N/A; advisory PR rating: <level>`. This advisory adaptation uses the same impact vocabulary for developer inconvenience; it is not a PE label, a course requirement, or a mark deduction. Do not turn missing tests or a design smell into a hypothetical product bug. If a concrete symptom is established, rate that symptom separately without double-counting it.

In the tables, bare levels mean conditional PE severities (`Low` means `severity.Low`); `Advisory` means PE severity is N/A. All ratings can change with evidence. Optional preferences with no demonstrated cost need no rating.

## Product behavior revealed by code

Basis: the documented contract, target-user workflows, and the [course testing guidance](course-notes.md#testing). PE reporting still requires evidence from the supplied release.

| Possible issue | Severity when supported | How to fix |
| --- | --- | --- |
| Core commands crash or the app cannot start in its supported environment | High only if almost unusable for most users; otherwise Low/Medium | Trace the failing path, validate assumptions, and recover from expected environment/input failures. |
| Save/load or export silently loses or corrupts data | Medium/High according to reach and recoverability | Preserve the previous data until a write succeeds; validate round trips and failed-write recovery. |
| A filtered/displayed index edits or deletes the wrong person | Medium; High only with the required broad impact | Resolve indices against the displayed list and verify identity before mutation. |
| Parser accepts/rejects input contrary to the contract, mishandles repeated prefixes, or consumes values as flags | Low/Medium | Align token boundaries and validation with the documented grammar; add focused valid/invalid cases. |
| Search mishandles documented case, whitespace, partial matching, or combined criteria | Low/Medium | Normalize consistently and correct predicate composition; test representative matches and nonmatches. |
| A rejected operation partially changes model/storage state, or mutable aliases bypass validation | Low/Medium; High only if broadly disabling | Validate before mutation and enforce invariants at the owning boundary; return defensive views/copies where needed. |
| Error feedback misidentifies the problem or gives unusable recovery instructions | Low/Medium | Report the actual failing condition and an actionable correction. |
| Delivered functionality blocks a normal target-user workflow despite following its specification | Low/Medium; High only if broadly disabling | Correct the feature design or supply the essential missing step; justify the need rather than proposing a preference. |

Choose `FunctionalityBug`, `FeatureFlaw`, or `DocumentationBug` using [the type definitions](pe.md#reporting-rules); a code change can reveal any of these.

## Java conventions and design

Basis: [Java rules](standards.md#java), [design lenses](course-notes.md#design-and-code-quality), and the [tP implementation rubric](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-grading.html).

| Possible violation or concern | Rating without a product symptom | How to fix |
| --- | --- | --- |
| Incorrect identifier case, nonpredicate boolean names, singular collection names, or inconsistent acronyms | Advisory VeryLow/Low | Rename to the applicable convention and update callers without changing behavior. |
| Wrong indentation, wrapping, brace style, spacing, or logical-block separation | Advisory VeryLow | Apply the course formatting rules to affected code; inspect the result rather than relying solely on IDE defaults. |
| Wildcard imports, inconsistent import order, misplaced array brackets, or unnecessarily broad variable scope | Advisory VeryLow/Low | Use explicit ordered imports, type-attached brackets, and the smallest valid variable scope. |
| Public mutable fields or leaked internal collections violate encapsulation | Advisory Low/Medium | Restrict mutation to the owner and expose suitable accessors or immutable views. |
| Required Javadoc is missing, misleading, or violates formatting/tag conventions | Advisory VeryLow/Low | Document the contract and relevant assumptions, respecting accessor, inherited-contract, and test exceptions. |
| Long/deeply nested methods mix abstraction levels; SRP/SoC responsibilities are tangled | Advisory Low/Medium | Extract cohesive operations with meaningful names and simplify control flow; length alone is not a violation. |
| Duplicated knowledge, magic values, speculative abstractions, or excessive coupling | Advisory Low/Medium | Centralize the shared rule, name meaningful constants, or simplify the dependency; explain the concrete maintenance cost. |
| A subtype breaks its parent's contract, or a boundary fails to enforce an invariant | Advisory Low/Medium until a symptom is established | Restore substitutability and validate at the responsible boundary; rate any demonstrated failure under product behavior. |
| Essential work occurs in assertions, exceptions are swallowed, or logs omit necessary failure context | Advisory Low/Medium until a symptom is established | Move required work outside assertions; handle/propagate failures and record useful context without exposing sensitive data. |

## Tests and QA

Basis: [testing lenses](course-notes.md#testing). These are coverage or reliability concerns, not proof of a released bug.

| Possible concern | Rating without a product symptom | How to fix |
| --- | --- | --- |
| Missing valid/invalid partitions, boundaries, or relevant receiver/state combinations | Advisory Low/Medium | Add the smallest cases targeting distinct plausible faults. |
| Tests assert only that parsing succeeds, or duplicate implementation logic instead of checking outcomes | Advisory Low/Medium | Assert resulting values, selected records, state changes, or failure behavior against an independent expectation. |
| Integration contracts or a previously fixed failure have no meaningful regression coverage | Advisory Low/Medium | Test the affected interaction or the concrete failing example at the appropriate level. |
| Tests depend on wall-clock time, ordering, shared data, or the developer's environment | Advisory Low/Medium | Control dependencies and isolate fixtures; verify the scenario deterministically. |
| Coverage percentages or test names claim behavior that assertions do not establish | Advisory Low | Correct the claim and add the missing behavioral check where valuable. |

## Documentation and Git

Basis: [Markdown](standards.md#github-flavored-markdown), [documentation prose](standards.md#documentation-prose), [Git](standards.md#git), and [documentation/UML lenses](course-notes.md#documentation-and-uml).

| Possible issue or violation | Severity/rating | How to fix |
| --- | --- | --- |
| Markdown violates list, heading, quote, or emphasis conventions but renders correctly | Advisory VeryLow | Apply the specified GFM conventions; inspect the actual renderer. |
| Rendered UG/DG has cosmetic typos, spacing, or layout inconsistencies | VeryLow if purely cosmetic | Correct the text/layout and recheck the rendered page. |
| UG/DG commands, examples, prerequisites, or links mislead or obstruct the reader | Low/Medium; High only if almost unusable for most readers | Align instructions with supported behavior, provide the missing step, and validate examples/links. |
| UML or requirements misrepresent the design, omit needed alternatives, or state unmeasurable NFRs | Low/Medium when the rendered UG/DG hinders readers; otherwise advisory Low | Correct notation/contracts, clarify intentional omissions, and make requirements testable. |
| Prose is ambiguous, overly verbose, or uses unexplained terminology | Low/Medium only with a concrete UG/DG reading obstacle; otherwise advisory Low | Rewrite for the target audience using the applicable style guidance; do not report editorial preference as a bug. |
| Commit subjects/bodies or branch names violate Git conventions | Advisory VeryLow/Low | Use a compliant subject/branch name and explain motivation for nontrivial commits; do not rewrite published history without authorization. |

Keep the final review specific to the inspected change. Each reported issue needs a location, basis, evidence, impact-based rating, and fix; do not paste the whole catalogue into a PR review.
