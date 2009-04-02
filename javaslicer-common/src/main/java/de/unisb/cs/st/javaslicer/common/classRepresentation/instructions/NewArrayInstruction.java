package de.unisb.cs.st.javaslicer.common.classRepresentation.instructions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.objectweb.asm.Opcodes;

import de.hammacher.util.StringCacheInput;
import de.hammacher.util.StringCacheOutput;
import de.hammacher.util.streams.OptimizedDataInputStream;
import de.hammacher.util.streams.OptimizedDataOutputStream;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionInstance;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionInstanceFactory;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionInstanceInfo;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionType;
import de.unisb.cs.st.javaslicer.common.classRepresentation.ReadMethod;
import de.unisb.cs.st.javaslicer.common.classRepresentation.TraceIterationInformationProvider;
import de.unisb.cs.st.javaslicer.common.classRepresentation.ReadMethod.MethodReadInformation;
import de.unisb.cs.st.javaslicer.common.exceptions.TracerException;

/**
 * Class representing a NEWARRAY instruction.
 *
 * @author Clemens Hammacher
 */
public class NewArrayInstruction extends AbstractInstruction {

    public static class NewArrayInstrInstanceInfo implements InstructionInstanceInfo {

        private final long newObjectIdentifier;

        public NewArrayInstrInstanceInfo(long newObjId) {
            this.newObjectIdentifier = newObjId;
        }

        public long getNewObjectIdentifier() {
            return this.newObjectIdentifier;
        }

        @Override
        public String toString() {
            if (this.newObjectIdentifier == -1)
                return "";
            return new StringBuilder(10).append('[').
                append(this.newObjectIdentifier).append(']').toString();
        }

        @Override
        public int hashCode() {
            return (int)this.newObjectIdentifier;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            NewArrayInstrInstanceInfo other = (NewArrayInstrInstanceInfo) obj;
            if (this.newObjectIdentifier != other.newObjectIdentifier)
                return false;
            return true;
        }

    }

    private final int arrayElemType;
    private final int newObjectIdentifierSequenceIndex;

    public NewArrayInstruction(final ReadMethod readMethod, final int lineNumber,
            final int arrayElemType, int newObjIdSeqIndex) {
        super(readMethod, Opcodes.NEWARRAY, lineNumber);
        assert arrayElemType == Opcodes.T_BOOLEAN
            || arrayElemType == Opcodes.T_CHAR
            || arrayElemType == Opcodes.T_FLOAT
            || arrayElemType == Opcodes.T_DOUBLE
            || arrayElemType == Opcodes.T_BYTE
            || arrayElemType == Opcodes.T_SHORT
            || arrayElemType == Opcodes.T_INT
            || arrayElemType == Opcodes.T_LONG;
        this.arrayElemType = arrayElemType;
        this.newObjectIdentifierSequenceIndex = newObjIdSeqIndex;
    }

    private NewArrayInstruction(final ReadMethod readMethod, final int lineNumber,
            final int arrayElemType, final int index, int newObjIdSeqIndex) {
        super(readMethod, Opcodes.NEWARRAY, lineNumber, index);
        assert arrayElemType == Opcodes.T_BOOLEAN
            || arrayElemType == Opcodes.T_CHAR
            || arrayElemType == Opcodes.T_FLOAT
            || arrayElemType == Opcodes.T_DOUBLE
            || arrayElemType == Opcodes.T_BYTE
            || arrayElemType == Opcodes.T_SHORT
            || arrayElemType == Opcodes.T_INT
            || arrayElemType == Opcodes.T_LONG;
        this.arrayElemType = arrayElemType;
        this.newObjectIdentifierSequenceIndex = newObjIdSeqIndex;
    }

    /**
     * Returns the type of the array's elements.
     *
     * The integer is one of these constants:
     * <ul>
     *   <li>org.objectweb.asm.Opcodes.T_BOOLEAN (4)</li>
     *   <li>org.objectweb.asm.Opcodes.T_CHAR (5)</li>
     *   <li>org.objectweb.asm.Opcodes.T_FLOAT (6)</li>
     *   <li>org.objectweb.asm.Opcodes.T_DOUBLE (7)</li>
     *   <li>org.objectweb.asm.Opcodes.T_BYTE (8)</li>
     *   <li>org.objectweb.asm.Opcodes.T_SHORT (9)</li>
     *   <li>org.objectweb.asm.Opcodes.T_INT (10)</li>
     *   <li>org.objectweb.asm.Opcodes.T_LONG (11)</li>
     * </ul>
     *
     * @return the type the array's elements
     */
    public int getArrayElemType() {
        return this.arrayElemType;
    }

    public InstructionType getType() {
        return InstructionType.NEWARRAY;
    }

    @Override
    public InstructionInstance getNextInstance(TraceIterationInformationProvider infoProv,
            int stackDepth, long instanceNr, InstructionInstanceFactory instanceFactory)
            throws TracerException {

        final long objectId = this.newObjectIdentifierSequenceIndex == -1 ? -1 :
            infoProv.getNextLong(this.newObjectIdentifierSequenceIndex);
        return instanceFactory.createInstructionInstance(this,
            infoProv.getNextInstructionOccurenceNumber(getIndex()),
            stackDepth, instanceNr, new NewArrayInstrInstanceInfo(objectId));
    }

    @Override
    public void writeOut(final DataOutputStream out, final StringCacheOutput stringCache) throws IOException {
        super.writeOut(out, stringCache);
        OptimizedDataOutputStream.writeInt0(this.arrayElemType, out);
        OptimizedDataOutputStream.writeInt0(this.newObjectIdentifierSequenceIndex, out);
    }

    public static NewArrayInstruction readFrom(final DataInputStream in, final MethodReadInformation methodInfo,
            @SuppressWarnings("unused") final StringCacheInput stringCache,
            @SuppressWarnings("unused") final int opcode,
            final int index, final int lineNumber) throws IOException {
        final int arrayElemType = OptimizedDataInputStream.readInt0(in);
        int newObjIdSeqIndex = OptimizedDataInputStream.readInt0(in);
        return new NewArrayInstruction(methodInfo.getMethod(), lineNumber, arrayElemType, index, newObjIdSeqIndex);
    }

    @Override
    public String toString() {
        String elemType;
        switch (this.arrayElemType) {
        case Opcodes.T_BOOLEAN:
            elemType = "T_BOOLEAN";
            break;
        case Opcodes.T_CHAR:
            elemType = "T_CHAR";
            break;
        case Opcodes.T_FLOAT:
            elemType = "T_FLOAT";
            break;
        case Opcodes.T_DOUBLE:
            elemType = "T_DOUBLE";
            break;
        case Opcodes.T_BYTE:
            elemType = "T_BYTE";
            break;
        case Opcodes.T_SHORT:
            elemType = "T_SHORT";
            break;
        case Opcodes.T_INT:
            elemType = "T_INT";
            break;
        case Opcodes.T_LONG:
            elemType = "T_LONG";
            break;
        default:
            elemType = "--ERROR--";
        }
        return new StringBuilder(elemType.length() + 9).append("NEWARRAY ").append(elemType).toString();
    }

}
