(:*******************************************************:)
(: Test: K-DateTimeFunc-5                                :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:39+02:00                       :)
(: Purpose: Passing the empty sequence as first argument is allowed(recent change in the specification). :)
(:*******************************************************:)
empty(dateTime((), xs:time("08:05:23")))