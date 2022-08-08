1. **Look-Ahead Check(Blacklist)**
   A look-ahead stage to validate input stream during the deserialization process to secure application. If the class in the blacklist is found during the deserialization process, the deserialization process will be terminated.
   We can find this kind of strategy using in Jackson/Weblogic and project SerialKiller.
2. **JEP290(Filter Incoming Serialization Data)**
   Allow incoming streams of object-serialization data to be filtered in order to improve both security and robustness.
   Define a global filter that can be configured by properties or a configuration file.
   The filter interface methods are called during the deserialization process to validate the classes being deserialized. The filter returns a status to accept, reject, or leave the status undecided, allowed or disallowed.
3. **Runtime Application Self-protection(RASP)**
   RASP is a security technology that is built or linked into an application or application runtime environment, and is capable of controlling application execution and detecting and preventing real-time attacks.
   Dose not need to build lists of patterns (blacklists) to match against the payloads, since they provide protection by design.
   Most of policies of RASP only focus on insecure deserialization attacks that try to execute commands and using input data that has been provided by the network request.



But there are some flaws in these defense solutions:

- If we find a new gadget,we can bypass lots of blacklists.
- Most Security researcher like to find gadget which eventually invoke common-dangerous functions such as `ProcessBuilder.exec()`,and some defense solutions only focus on these functions(even RASP),if we find a new fundamental vector in Java,we can find many new gadgets and bypass most of Java deserialization defense solutions.