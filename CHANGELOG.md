# Change Log

## next

* [rguenthe](https://github.com/rguenthe) Add [code coverage api](https://github.com/jenkinsci/code-coverage-api-plugin) line coverage support [#60](https://github.com/SxMShaDoW/embeddable-badges-plugin/pull/60)

## 2.5.2
[chetanddesai](https://github.com/chetanddesai)

* [YogiKhan](https://github.com/YogiKhan) Add view support [#54](https://github.com/SxMShaDoW/embeddable-badges-plugin/pull/54)
* [vijkris99](https://github.com/vijkris99) Fix badge truncation bug [#52](https://github.com/SxMShaDoW/embeddable-badges-plugin/pull/52)

## 2.5.1
[chetanddesai](https://github.com/chetanddesai)

* Fix broken Jenkins job URLs on Enterprise Jenkins [#45](https://github.com/SxMShaDoW/embeddable-badges-plugin/issues/45).


## 2.5
[chetanddesai](https://github.com/chetanddesai)

* First pass on dynamically scaling images [#40](https://github.com/SxMShaDoW/embeddable-badges-plugin/issues/40).

## 2.4
[chetanddesai](https://github.com/chetanddesai)

* Fix division cast issue on [#35](https://github.com/SxMShaDoW/embeddable-badges-plugin/issues/35).
* Add in a new build description badge [#34](https://github.com/SxMShaDoW/embeddable-badges-plugin/issues/34).

## 2.3
[chetanddesai](https://github.com/chetanddesai)

* Fix the colour bug on the test icon. See issue [#35](https://github.com/SxMShaDoW/embeddable-badges-plugin/issues/35).

## 2.2
[chetanddesai](https://github.com/chetanddesai)

* Subtract skipped tests from passed count, so if you skip `9` tests and `12` pass, it will read `12/21` instead of `21/21`.
* Scale `tests` image to support up to 9999 tests.
* Report `0` for no coverage results found instead of `na%`.

## 2.1
[chetanddesai](https://github.com/chetanddesai)

* Change coverage action data from `getBuildHealth().getScore()` to line percentage for Cobertura, and element coverage percentage for Clover.

## 2.0

* Lost in the ether. Who knows.

## 1.0

* Initial Release
